package live.pvphard.practice.core.database.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.core.profile.data.types.*;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.ProfileData;
import live.pvphard.practice.core.profile.enums.ChatChannel;
import live.pvphard.practice.core.profile.enums.WorldTime;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.serializer.Serializer;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
@UtilityClass
public class MongoUtility {
    private static final String EMPTY_STRING = "";
    private static final int DEFAULT_ELO = 1000;
    private static final int DEFAULT_COINS = 100;
    private static final int DEFAULT_INT = 0;
    private static final long DEFAULT_LONG = 0L;
    private static final boolean DEFAULT_BOOLEAN_TRUE = true;
    private static final boolean DEFAULT_BOOLEAN_FALSE = false;

    /**
     * Represents the result of a validation operation.
     */
    @Getter
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(List<String> errors) {
            return new ValidationResult(false, errors);
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }

    /**
     * Validates a Profile object with comprehensive checks.
     *
     * @param profile The Profile object to validate.
     * @return A ValidationResult indicating whether the profile is valid or not.
     */
    public ValidationResult validateProfile(Profile profile) {
        List<String> errors = new ArrayList<>();

        if (profile == null) {
            errors.add("Perfil nao pode ser nulo");
            return ValidationResult.invalid(errors);
        }

        if (profile.getUuid() == null) {
            errors.add("UUID do perfil nao pode ser nulo");
        }

        if (profile.getName() == null || profile.getName().trim().isEmpty()) {
            errors.add("Nome do perfil nao pode ser nulo ou vazio");
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    /**
     * Converts a Profile object to a MongoDB Document with comprehensive validation and null safety.
     *
     * @param profile The Profile object to convert.
     * @return A Document representation of the Profile.
     * @throws IllegalArgumentException if the profile is invalid.
     */
    public Document toDocument(Profile profile) {
        ValidationResult validation = validateProfile(profile);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Falha na validacao do perfil: " + validation.getErrors());
        }

        try {
            Document document = new Document();
            document.put("firstJoin", profile.getFirstJoin());
            document.put("uuid", profile.getUuid().toString());
            document.put("name", safeString(profile.getName()));

            ProfileData profileData = profile.getProfileData();
            if (profileData != null) {
                document.put("profileData", convertProfileData(profileData));
            } else {
                Logger.warn(String.format("ProfileData esta nulo para o perfil: %s, usando documento vazio", profile.getUuid()));
                document.put("profileData", new Document());
            }

            return document;
        } catch (Exception e) {
            Logger.logException(String.format("Falha ao converter perfil para documento no UUID: %s", profile.getUuid()), e);
            throw new RuntimeException("Falha ao converter perfil para documento", e);
        }
    }

    /**
     * Updates a Profile object from a MongoDB Document with comprehensive error handling.
     *
     * @param profile  The Profile object to update.
     * @param document The Document containing the profile data.
     * @throws IllegalArgumentException if the profile or document is null.
     */
    public void updateProfileFromDocument(Profile profile, Document document) {
        if (profile == null) {
            throw new IllegalArgumentException("Perfil nao pode ser nulo");
        }
        if (document == null) {
            throw new IllegalArgumentException("Documento nao pode ser nulo");
        }

        try {
            Long firstJoin = document.getLong("firstJoin");
            if (firstJoin != null) {
                profile.setFirstJoin(firstJoin);
            }

            Document profileDataDocument = document.get("profileData", Document.class);
            if (profileDataDocument != null) {
                ProfileData profileData = parseProfileData(profileDataDocument);
                profile.setProfileData(profileData);
            } else {
                Logger.warn(String.format("Documento ProfileData esta nulo para o perfil: %s, criando novo ProfileData", profile.getUuid()));
                profile.setProfileData(new ProfileData());
            }
        } catch (Exception e) {
            Logger.logException(String.format("Erro ao atualizar perfil a partir do documento no UUID: %s", profile.getUuid()), e);
            throw new RuntimeException("Falha ao atualizar perfil a partir do documento", e);
        }
    }

    /**
     * Converts ProfileData to a MongoDB Document with comprehensive null safety.
     *
     * @param profileData The ProfileData object to convert.
     * @return A Document representation of the ProfileData.
     */
    private static Document convertProfileData(ProfileData profileData) {
        return new DocumentBuilder()
                .put("elo", profileData.getElo())
                .put("experience", profileData.getExperience())
                .put("coins", profileData.getCoins())
                .put("unrankedWins", profileData.getUnrankedWins())
                .put("unrankedLosses", profileData.getUnrankedLosses())
                .put("rankedWins", profileData.getRankedWins())
                .put("rankedLosses", profileData.getRankedLosses())
                .put("winStreak", profileData.getWinStreak())
                .put("bestWinStreak", profileData.getBestWinStreak())
                .put("rankedBanned", profileData.isRankedBanned())
                .put("globalLevel", safeString(profileData.getGlobalLevel()))
                .put("selectedTitle", safeString(profileData.getSelectedTitle()))
                .put("unlockedTitles", safeList(profileData.getUnlockedTitles()))
                .putSafe("unrankedKitData", profileData::getUnrankedKitData, MongoUtility::convertUnrankedKitData)
                .putSafe("rankedKitData", profileData::getRankedKitData, MongoUtility::convertRankedKitData)
                .putSafe("ffaData", profileData::getFfaData, MongoUtility::convertFFAData)
                .putSafe("layoutData", profileData::getLayoutData, MongoUtility::convertLayoutData)
                .putSafe("settingData", profileData::getSettingData, MongoUtility::convertProfileSettingData)
                .putSafe("cosmeticData", profileData::getCosmeticData, MongoUtility::convertProfileCosmeticData)
                .putSafe("playTimeData", profileData::getPlayTimeData, MongoUtility::convertProfilePlayTimeData)
                .build();
    }

    /**
     * Parses a MongoDB Document into a ProfileData object with comprehensive null safety.
     *
     * @param profileDataDocument The Document containing the profile data.
     * @return A ProfileData object populated with the data from the Document.
     */
    private static ProfileData parseProfileData(Document profileDataDocument) {
        ProfileData profileData = new ProfileData();

        profileData.setElo(profileDataDocument.getInteger("elo", DEFAULT_ELO));
        profileData.setExperience(profileDataDocument.getInteger("experience", DEFAULT_INT));
        profileData.setCoins(profileDataDocument.getInteger("coins", DEFAULT_COINS));
        profileData.setUnrankedWins(profileDataDocument.getInteger("unrankedWins", DEFAULT_INT));
        profileData.setUnrankedLosses(profileDataDocument.getInteger("unrankedLosses", DEFAULT_INT));
        profileData.setRankedWins(profileDataDocument.getInteger("rankedWins", DEFAULT_INT));
        profileData.setRankedLosses(profileDataDocument.getInteger("rankedLosses", DEFAULT_INT));
        profileData.setWinStreak(profileDataDocument.getInteger("winStreak", DEFAULT_INT));
        profileData.setBestWinStreak(profileDataDocument.getInteger("bestWinStreak", DEFAULT_INT));
        profileData.setRankedBanned(profileDataDocument.getBoolean("rankedBanned", DEFAULT_BOOLEAN_FALSE));
        profileData.setGlobalLevel(profileDataDocument.getString("globalLevel"));
        profileData.setSelectedTitle(profileDataDocument.getString("selectedTitle"));

        List<String> unlockedTitles = profileDataDocument.getList("unlockedTitles", String.class);
        if (unlockedTitles != null) {
            profileData.setUnlockedTitles(new ArrayList<>(unlockedTitles));
        }

        parseAndMerge(profileDataDocument, "unrankedKitData", MongoUtility::parseUnrankedKitData,
                profileData.getUnrankedKitData(), profileData::setUnrankedKitData);
        parseAndMerge(profileDataDocument, "rankedKitData", MongoUtility::parseRankedKitData,
                profileData.getRankedKitData(), profileData::setRankedKitData);
        parseAndMerge(profileDataDocument, "ffaData", MongoUtility::parseFFAData,
                profileData.getFfaData(), profileData::setFfaData);

        parseAndSet(profileDataDocument, "layoutData", MongoUtility::parseProfileLayoutData,
                profileData::setLayoutData, ProfileLayoutData::new);
        parseAndSet(profileDataDocument, "settingData", MongoUtility::parseProfileSettingData,
                profileData::setSettingData, ProfileSettingData::new);
        parseAndSet(profileDataDocument, "cosmeticData", MongoUtility::parseProfileCosmeticData,
                profileData::setCosmeticData, ProfileCosmeticData::new);
        parseAndSet(profileDataDocument, "playTimeData", MongoUtility::parseProfilePlayTimeData,
                profileData::setPlayTimeData, ProfilePlayTimeData::new);

        return profileData;
    }

    /**
     * Converts a map of unranked kit data to a MongoDB Document.
     * Each kit entry contains division, tier, wins, losses, and winstreak information.
     *
     * @param kitData A map where the key is the kit name and the value is ProfileUnrankedKitData
     * @return A Document representation of the unranked kit data, or empty Document if input is null
     */
    private static Document convertUnrankedKitData(Map<String, ProfileUnrankedKitData> kitData) {
        Document kitDataDocument = new Document();
        if (kitData == null) return kitDataDocument;

        kitData.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .forEach(entry -> {
                    ProfileUnrankedKitData data = entry.getValue();
                    Document kitEntry = new DocumentBuilder()
                            .put("division", data.getDivision() != null ? data.getDivision().getName() : EMPTY_STRING)
                            .put("tier", data.getTier() != null ? data.getTier().getName() : EMPTY_STRING)
                            .put("wins", data.getWins())
                            .put("losses", data.getLosses())
                            .put("winstreak", data.getWinstreak())
                            .put("bestWinstreak", data.getBestWinstreak())
                            .build();
                    kitDataDocument.put(entry.getKey(), kitEntry);
                });

        return kitDataDocument;
    }

    /**
     * Converts a map of ranked kit data to a MongoDB Document.
     * Each kit entry contains elo, wins, and losses information.
     *
     * @param kitData A map where the key is the kit name and the value is ProfileRankedKitData
     * @return A Document representation of the ranked kit data, or empty Document if input is null
     */
    private static Document convertRankedKitData(Map<String, ProfileRankedKitData> kitData) {
        Document kitDataDocument = new Document();
        if (kitData == null) return kitDataDocument;

        kitData.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .forEach(entry -> {
                    ProfileRankedKitData data = entry.getValue();
                    Document kitEntry = new DocumentBuilder()
                            .put("elo", data.getElo())
                            .put("wins", data.getWins())
                            .put("losses", data.getLosses())
                            .build();
                    kitDataDocument.put(entry.getKey(), kitEntry);
                });

        return kitDataDocument;
    }

    /**
     * Converts a map of FFA (Free For All) data to a MongoDB Document.
     * Each FFA entry contains kills, deaths, killstreak, and highest killstreak information.
     *
     * @param ffaData A map where the key is the arena/mode name and the value is ProfileFFAData
     * @return A Document representation of the FFA data, or empty Document if input is null
     */
    private static Document convertFFAData(Map<String, ProfileFFAData> ffaData) {
        Document ffaDataDocument = new Document();
        if (ffaData == null) return ffaDataDocument;

        ffaData.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .forEach(entry -> {
                    ProfileFFAData data = entry.getValue();
                    Document ffaEntry = new DocumentBuilder()
                            .put("kills", data.getKills())
                            .put("deaths", data.getDeaths())
                            .put("killstreak", data.getKillstreak())
                            .put("highestKillstreak", data.getHighestKillstreak())
                            .build();
                    ffaDataDocument.put(entry.getKey(), ffaEntry);
                });

        return ffaDataDocument;
    }

    /**
     * Converts ProfileLayoutData to a MongoDB Document.
     * Serializes layout configurations including item arrangements and display names.
     *
     * @param layoutData The ProfileLayoutData object containing layout configurations
     * @return A Document representation of the layout data, or empty Document if input is null
     */
    private static Document convertLayoutData(ProfileLayoutData layoutData) {
        Document layoutDocument = new Document();
        if (layoutData == null || layoutData.getLayouts() == null) return layoutDocument;

        layoutData.getLayouts().entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .forEach(entry -> {
                    List<Document> layoutRecords = entry.getValue().stream()
                            .filter(Objects::nonNull)
                            .map(record -> new DocumentBuilder()
                                    .put("name", safeString(record.getName()))
                                    .put("displayName", safeString(record.getDisplayName()))
                                    .put("items", record.getItems() != null ?
                                            Serializer.serializeItemStack(record.getItems()) : EMPTY_STRING)
                                    .build())
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                    layoutDocument.put(entry.getKey(), layoutRecords);
                });

        return layoutDocument;
    }

    /**
     * Converts ProfileSettingData to a MongoDB Document.
     * Includes all user preference settings such as party messages, scoreboard visibility, etc.
     *
     * @param settingData The ProfileSettingData object containing user settings
     * @return A Document representation of the settings data, or empty Document if input is null
     */
    private static Document convertProfileSettingData(ProfileSettingData settingData) {
        if (settingData == null) return new Document();

        return new DocumentBuilder()
                .put("partyMessagesEnabled", settingData.isPartyMessagesEnabled())
                .put("partyInvitesEnabled", settingData.isPartyInvitesEnabled())
                .put("scoreboardEnabled", settingData.isScoreboardEnabled())
                .put("tablistEnabled", settingData.isTablistEnabled())
                .put("showScoreboardLines", settingData.isShowScoreboardLines())
                .put("receiveDuelRequestsEnabled", settingData.isReceiveDuelRequestsEnabled())
                .put("serverTitles", settingData.isServerTitles())
                .put("pingRange", settingData.getPingRange())
                .put("chatChannel", safeString(settingData.getChatChannel()))
                .put("time", safeString(settingData.getTime()))
                .build();
    }

    /**
     * Converts ProfileCosmeticData to a MongoDB Document.
     * Includes selected cosmetic items such as kill effects, messages, and trails.
     *
     * @param cosmeticData The ProfileCosmeticData object containing cosmetic selections
     * @return A Document representation of the cosmetic data, or empty Document if input is null
     */
    private static Document convertProfileCosmeticData(ProfileCosmeticData cosmeticData) {
        if (cosmeticData == null) return new Document();

        Document selectedCosmetics = new Document();
        for (Map.Entry<CosmeticType, String> entry : cosmeticData.getSelectedCosmetics().entrySet()) {
            selectedCosmetics.put(entry.getKey().name(), safeString(entry.getValue()));
        }

        return new DocumentBuilder()
                .put("selectedCosmetics", selectedCosmetics)
                .put("selectedKillEffect", safeString(cosmeticData.getSelectedKillEffect()))
                .put("selectedKillMessage", safeString(cosmeticData.getSelectedKillMessage()))
                .put("selectedSoundEffect", safeString(cosmeticData.getSelectedSoundEffect()))
                .put("selectedProjectileTrail", safeString(cosmeticData.getSelectedProjectileTrail()))
                .put("selectedSuit", safeString(cosmeticData.getSelectedSuit()))
                .put("selectedCloak", safeString(cosmeticData.getSelectedCloak()))
                .build();
    }

    /**
     * Converts ProfilePlayTimeData to a MongoDB Document.
     * Includes total playtime and last login information.
     *
     * @param playTimeData The ProfilePlayTimeData object containing playtime statistics
     * @return A Document representation of the playtime data, or empty Document if input is null
     */
    private static Document convertProfilePlayTimeData(ProfilePlayTimeData playTimeData) {
        if (playTimeData == null) return new Document();

        return new DocumentBuilder()
                .put("total", playTimeData.getTotal())
                .put("lastLogin", playTimeData.getLastLogin())
                .build();
    }


    /**
     * Parses a MongoDB Document into a map of unranked kit data.
     * Reconstructs ProfileUnrankedKitData objects with division, tier, and statistics.
     * Uses DivisionService to validate and resolve division/tier references.
     *
     * @param kitDataDocument The Document containing unranked kit data
     * @return A map of kit names to ProfileUnrankedKitData objects, empty map if input is null
     */
    private static Map<String, ProfileUnrankedKitData> parseUnrankedKitData(Document kitDataDocument) {
        Map<String, ProfileUnrankedKitData> kitData = new HashMap<>();
        if (kitDataDocument == null) return kitData;

        DivisionService divisionService = HardPractice.getInstance().getService(DivisionService.class);

        kitDataDocument.forEach((key, value) -> {
            try {
                Document kitEntry = (Document) value;
                ProfileUnrankedKitData kit = new ProfileUnrankedKitData();

                String storedDivision = kitEntry.getString("division");
                if (storedDivision != null && !storedDivision.isEmpty()) {
                    Division division = divisionService.getDivision(storedDivision);
                    if (division != null) {
                        kit.setDivision(division.getName());

                        String storedTier = kitEntry.getString("tier");
                        if (storedTier != null && !storedTier.isEmpty()) {
                            division.getTiers().stream()
                                    .filter(t -> t.getName().equals(storedTier))
                                    .findFirst()
                                    .ifPresent(tier -> kit.setTier(tier.getName()));
                        }
                    }
                }

                kit.setWins(kitEntry.getInteger("wins", DEFAULT_INT));
                kit.setLosses(kitEntry.getInteger("losses", DEFAULT_INT));
                kit.setWinstreak(kitEntry.getInteger("winstreak", DEFAULT_INT));
                kit.setBestWinstreak(kitEntry.getInteger("bestWinstreak", DEFAULT_INT));

                kitData.put(key, kit);
            } catch (Exception e) {
                Logger.logException(String.format("Falha ao interpretar os dados do kit unranked para a chave: %s", key), e);
            }
        });

        return kitData;
    }


    /**
     * Parses a MongoDB Document into a map of ranked kit data.
     * Reconstructs ProfileRankedKitData objects with elo ratings and match statistics.
     *
     * @param kitDataDocument The Document containing ranked kit data
     * @return A map of kit names to ProfileRankedKitData objects, empty map if input is null
     */
    private static Map<String, ProfileRankedKitData> parseRankedKitData(Document kitDataDocument) {
        Map<String, ProfileRankedKitData> kitData = new HashMap<>();
        if (kitDataDocument == null) return kitData;

        kitDataDocument.forEach((key, value) -> {
            try {
                Document kitEntry = (Document) value;
                ProfileRankedKitData kit = new ProfileRankedKitData();

                kit.setElo(kitEntry.getInteger("elo", DEFAULT_ELO));
                kit.setWins(kitEntry.getInteger("wins", DEFAULT_INT));
                kit.setLosses(kitEntry.getInteger("losses", DEFAULT_INT));

                kitData.put(key, kit);
            } catch (Exception e) {
                Logger.logException(String.format("Falha ao interpretar os dados do kit ranked para a chave: %s", key), e);
            }
        });

        return kitData;
    }

    /**
     * Parses a MongoDB Document into a map of FFA data.
     * Reconstructs ProfileFFAData objects with kill/death statistics and streaks.
     *
     * @param ffaDataDocument The Document containing FFA data
     * @return A map of arena/mode names to ProfileFFAData objects, empty map if input is null
     */
    private static Map<String, ProfileFFAData> parseFFAData(Document ffaDataDocument) {
        Map<String, ProfileFFAData> ffaData = new HashMap<>();
        if (ffaDataDocument == null) return ffaData;

        ffaDataDocument.forEach((key, value) -> {
            try {
                Document ffaEntry = (Document) value;
                ProfileFFAData ffa = new ProfileFFAData();

                ffa.setKills(ffaEntry.getInteger("kills", DEFAULT_INT));
                ffa.setDeaths(ffaEntry.getInteger("deaths", DEFAULT_INT));
                ffa.setKillstreak(ffaEntry.getInteger("killstreak", DEFAULT_INT));
                ffa.setHighestKillstreak(ffaEntry.getInteger("highestKillstreak", DEFAULT_INT));

                ffaData.put(key, ffa);
            } catch (Exception e) {
                Logger.logException(String.format("Falha ao interpretar os dados de FFA para a chave: %s", key), e);
            }
        });

        return ffaData;
    }

    /**
     * Parses a MongoDB Document into ProfileLayoutData.
     * Deserializes layout configurations and reconstructs ItemStack arrays from serialized strings.
     * Handles deserialization errors gracefully by logging and using empty ItemStack arrays.
     *
     * @param layoutDocument The Document containing layout data
     * @return A ProfileLayoutData object with reconstructed layouts, or new instance if input is null
     */
    private static ProfileLayoutData parseProfileLayoutData(Document layoutDocument) {
        ProfileLayoutData layoutData = new ProfileLayoutData();
        if (layoutDocument == null) return layoutData;

        layoutDocument.forEach((key, value) -> {
            try {
                List<LayoutData> layoutRecords = new ArrayList<>();
                ItemStack[] fallbackItems = getDefaultKitItems(key);
                @SuppressWarnings("unchecked")
                List<Document> records = (List<Document>) value;

                if (records != null) {
                    records.stream()
                            .filter(Objects::nonNull)
                            .forEach(record -> {
                                String name = record.getString("name");
                                String displayName = record.getString("displayName");
                                String itemsString = record.getString("items");

                                ItemStack[] items = null;
                                if (itemsString != null && !itemsString.isEmpty()) {
                                    try {
                                        items = Serializer.deserializeItemStack(itemsString);
                                    } catch (Exception e) {
                                        Logger.logException(String.format("Falha ao desserializar os itens do layout: %s", name), e);
                                    }
                                }

                                if (!InventoryUtil.hasAnyItem(items)) {
                                    items = InventoryUtil.cloneItemStackArray(fallbackItems);
                                }

                                LayoutData layoutRecord = new LayoutData(
                                        safeString(name),
                                        safeString(displayName),
                                        items != null ? items : new ItemStack[0]
                                );
                                layoutRecords.add(layoutRecord);
                            });
                }

                if (!layoutRecords.isEmpty()) {
                    layoutData.getLayouts().put(key, layoutRecords);
                }
            } catch (Exception e) {
                Logger.logException(String.format("Falha ao interpretar os dados de layout para a chave: %s", key), e);
            }
        });

        return layoutData;
    }

    private static ItemStack[] getDefaultKitItems(String kitName) {
        if (kitName == null || kitName.trim().isEmpty()) {
            return new ItemStack[0];
        }

        KitService kitService;
        try {
            kitService = HardPractice.getInstance().getService(KitService.class);
        } catch (Exception ignored) {
            return new ItemStack[0];
        }

        if (kitService == null || kitService.getKits() == null) {
            return new ItemStack[0];
        }

        Kit matchedKit = kitService.getKits().stream()
                .filter(Objects::nonNull)
                .filter(kit -> kit.getName() != null && kit.getName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);

        if (matchedKit == null) {
            return new ItemStack[0];
        }

        return InventoryUtil.getEditableKitItems(matchedKit);
    }


    /**
     * Parses a MongoDB Document into ProfileSettingData.
     * Reconstructs all user preference settings with appropriate default values.
     * Validates enum values for ChatChannel and WorldTime settings.
     *
     * @param settingDocument The Document containing settings data
     * @return A ProfileSettingData object with user preferences, or new instance with defaults if input is null
     */
    private static ProfileSettingData parseProfileSettingData(Document settingDocument) {
        ProfileSettingData settingData = new ProfileSettingData();
        if (settingDocument == null) return settingData;

        settingData.setPartyMessagesEnabled(settingDocument.getBoolean("partyMessagesEnabled", DEFAULT_BOOLEAN_TRUE));
        settingData.setPartyInvitesEnabled(settingDocument.getBoolean("partyInvitesEnabled", DEFAULT_BOOLEAN_TRUE));
        settingData.setScoreboardEnabled(settingDocument.getBoolean("scoreboardEnabled", DEFAULT_BOOLEAN_TRUE));
        settingData.setTablistEnabled(settingDocument.getBoolean("tablistEnabled", DEFAULT_BOOLEAN_TRUE));
        settingData.setShowScoreboardLines(settingDocument.getBoolean("showScoreboardLines", DEFAULT_BOOLEAN_TRUE));
        settingData.setReceiveDuelRequestsEnabled(settingDocument.getBoolean("receiveDuelRequestsEnabled", DEFAULT_BOOLEAN_TRUE));
        settingData.setServerTitles(settingDocument.getBoolean("serverTitles", DEFAULT_BOOLEAN_TRUE));
        settingData.setPingRange(settingDocument.getInteger("pingRange", 100));

        String chatChannel = settingDocument.getString("chatChannel");
        settingData.setChatChannel(chatChannel != null ? chatChannel : ChatChannel.GLOBAL.toString());

        String time = settingDocument.getString("time");
        settingData.setTime(time != null ? time : WorldTime.DEFAULT.getName());

        return settingData;
    }


    /**
     * Parses a MongoDB Document into ProfileCosmeticData.
     * Reconstructs selected cosmetic items from stored string identifiers.
     *
     * @param cosmeticDocument The Document containing cosmetic data
     * @return A ProfileCosmeticData object with cosmetic selections, or new instance if input is null
     */
    private static ProfileCosmeticData parseProfileCosmeticData(Document cosmeticDocument) {
        ProfileCosmeticData cosmeticData = new ProfileCosmeticData();
        if (cosmeticDocument == null) return cosmeticData;

        Document selectedCosmetics = cosmeticDocument.get("selectedCosmetics", Document.class);
        if (selectedCosmetics != null) {
            for (CosmeticType type : CosmeticType.values()) {
                String value = selectedCosmetics.getString(type.name());
                if (value != null) {
                    cosmeticData.getSelectedCosmetics().put(type, value);
                }
            }
            return cosmeticData;
        }

        for (CosmeticType type : CosmeticType.values()) {
            String legacyFieldName = getLegacyFieldName(type);
            String value = cosmeticDocument.getString(legacyFieldName);
            if (value != null) {
                cosmeticData.getSelectedCosmetics().put(type, value);
            }
        }

        return cosmeticData;
    }

    /**
     * Parses a MongoDB Document into ProfilePlayTimeData.
     * Reconstructs playtime statistics with null-safe handling for Long values.
     *
     * @param playTimeDocument The Document containing playtime data
     * @return A ProfilePlayTimeData object with playtime statistics, or new instance with defaults if input is null
     */
    private static ProfilePlayTimeData parseProfilePlayTimeData(Document playTimeDocument) {
        ProfilePlayTimeData playTimeData = new ProfilePlayTimeData();
        if (playTimeDocument == null) return playTimeData;

        Long total = playTimeDocument.getLong("total");
        Long lastLogin = playTimeDocument.getLong("lastLogin");

        playTimeData.setTotal(total != null ? total : DEFAULT_LONG);
        playTimeData.setLastLogin(lastLogin != null ? lastLogin : DEFAULT_LONG);

        return playTimeData;
    }

    /**
     * Returns the legacy field name for a given CosmeticType.
     * This method is used to maintain backward compatibility with older database schemas.
     *
     * @param type The CosmeticType to get the legacy field name for
     * @return The legacy field name as a String
     */
    private static String getLegacyFieldName(CosmeticType type) {
        switch (type) {
            case KILL_EFFECT: return "selectedKillEffect";
            case KILL_MESSAGE: return "selectedKillMessage";
            case SOUND_EFFECT: return "selectedSoundEffect";
            case PROJECTILE_TRAIL: return "selectedProjectileTrail";
            case SUIT: return "selectedSuit";
            case CLOAK: return "selectedCloak";
            default: return "selected" + type.name();
        }
    }

    /**
     * Safe string handling that prevents null pointer exceptions.
     * Provides a consistent way to handle potentially null string values throughout the utility.
     *
     * @param value The string value to check
     * @return The original string if not null, otherwise an empty string
     */
    private static String safeString(String value) {
        return value != null ? value : EMPTY_STRING;
    }

    /**
     * Safe list handling that prevents null pointer exceptions.
     * Creates a defensive copy of the input list to prevent external modifications.
     *
     * @param list The list to check and copy
     * @param <T> The type of elements in the list
     * @return A new ArrayList containing the elements of the input list, or an empty list if input is null
     */
    private static <T> List<T> safeList(List<T> list) {
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    /**
     * Generic method for parsing and merging map data from a Document.
     * Provides a standardized way to handle map-based data fields with error recovery.
     * If parsing fails, the existing map is preserved to maintain data integrity.
     *
     * @param document The Document to parse from
     * @param key The key to look for in the Document
     * @param parser The function to parse the sub-document into a map
     * @param existingMap The existing map to merge parsed data into
     * @param setter The consumer to set the final merged map back to the parent object
     * @param <T> The type of values in the map
     */
    private static <T> void parseAndMerge(Document document, String key,
                                          Function<Document, Map<String, T>> parser,
                                          Map<String, T> existingMap,
                                          Consumer<Map<String, T>> setter) {
        try {
            Document subDocument = document.get(key, Document.class);
            if (subDocument != null) {
                Map<String, T> parsedData = parser.apply(subDocument);
                if (parsedData != null && !parsedData.isEmpty()) {
                    existingMap.putAll(parsedData);
                    setter.accept(existingMap);
                    return;
                }
            }
        } catch (Exception e) {
            Logger.logException(String.format("Falha ao interpretar e mesclar o campo: %s", key), e);
        }

        setter.accept(existingMap);
    }

    /**
     * Generic method for parsing a sub-document and setting it to a field.
     * Provides a standardized way to handle complex object fields with fallback to defaults.
     * Ensures robust error handling and prevents null values from being set.
     *
     * @param document The Document to parse from
     * @param key The key to look for in the Document
     * @param parser The function to parse the sub-document into the target type
     * @param setter The consumer to set the parsed value to the parent object
     * @param defaultSupplier A supplier to create a default value if parsing fails or document is null
     * @param <T> The type of the parsed value
     */
    private static <T> void parseAndSet(Document document, String key,
                                        Function<Document, T> parser,
                                        Consumer<T> setter,
                                        Supplier<T> defaultSupplier) {
        try {
            Document subDocument = document.get(key, Document.class);
            if (subDocument != null) {
                T parsed = parser.apply(subDocument);
                if (parsed != null) {
                    setter.accept(parsed);
                    return;
                }
            }
        } catch (Exception e) {
            Logger.logException(String.format("Falha ao interpretar e definir o campo: %s", key), e);
        }

        try {
            setter.accept(defaultSupplier.get());
        } catch (Exception e) {
            Logger.logException(String.format("Falha ao criar o valor padrao para o campo: %s", key), e);
        }
    }

    /**
     * A builder class for constructing MongoDB Documents with null safety and error handling.
     * This class allows for chaining method calls to build a Document incrementally.
     */
    private static class DocumentBuilder {
        private final Document document;

        public DocumentBuilder() {
            this.document = new Document();
        }

        public DocumentBuilder put(String key, Object value) {
            if (key != null) {
                document.put(key, value);
            }
            return this;
        }

        public <T> DocumentBuilder putSafe(String key, Supplier<T> supplier, Function<T, Document> converter) {
            try {
                T value = supplier.get();
                if (value != null) {
                    Document converted = converter.apply(value);
                    if (converted != null) {
                        document.put(key, converted);
                    }
                }
            } catch (Exception e) {
                Logger.logException(String.format("Falha ao inserir valor seguro para a chave: %s", key), e);
            }
            return this;
        }

        public Document build() {
            return document;
        }
    }
}
