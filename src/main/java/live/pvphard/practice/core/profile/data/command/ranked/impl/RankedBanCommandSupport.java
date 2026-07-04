package live.pvphard.practice.core.profile.data.command.ranked.impl;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.regex.Pattern;

final class RankedBanCommandSupport {
    private RankedBanCommandSupport() {
    }

    static ResolvedRankedProfile resolveTarget(HardPractice plugin, String input) {
        if (plugin == null || input == null || input.trim().isEmpty()) {
            return null;
        }

        ProfileService profileService = plugin.getService(ProfileService.class);
        if (profileService == null) {
            return null;
        }

        String trimmedInput = input.trim();
        UUID inputUuid = parseUuid(trimmedInput);
        Document document = findProfileDocument(profileService.getCollection(), trimmedInput, inputUuid);

        UUID resolvedUuid = inputUuid;
        String resolvedName = trimmedInput;
        boolean resolvedFromKnownSource = document != null;

        if (document != null) {
            UUID documentUuid = parseUuid(document.getString("uuid"));
            if (resolvedUuid == null) {
                resolvedUuid = documentUuid;
            }

            String documentName = safeString(document.getString("name"));
            if (!documentName.isEmpty()) {
                resolvedName = documentName;
            }
        }

        if (resolvedUuid == null) {
            OfflinePlayer offlinePlayer = PlayerUtil.getOfflinePlayerByName(trimmedInput);
            if (offlinePlayer != null && offlinePlayer.getUniqueId() != null) {
                resolvedUuid = offlinePlayer.getUniqueId();
                if (offlinePlayer.getName() != null && !offlinePlayer.getName().trim().isEmpty()) {
                    resolvedName = offlinePlayer.getName();
                }
                resolvedFromKnownSource = true;
            }
        }

        if (!resolvedFromKnownSource || resolvedUuid == null) {
            return null;
        }

        Profile profile = profileService.getProfile(resolvedUuid);
        if (profile == null) {
            return null;
        }

        if (profile.getName() == null || profile.getName().trim().isEmpty()) {
            profile.setName(resolvedName);
        }

        return new ResolvedRankedProfile(profile, safeString(resolvedName).isEmpty() ? resolvedUuid.toString() : resolvedName);
    }

    private static Document findProfileDocument(MongoCollection<Document> collection, String input, UUID inputUuid) {
        if (collection == null) {
            return null;
        }

        if (inputUuid != null) {
            Document byUuid = collection.find(Filters.eq("uuid", inputUuid.toString())).first();
            if (byUuid != null) {
                return byUuid;
            }
        }

        Pattern namePattern = Pattern.compile("^" + Pattern.quote(input) + "$", Pattern.CASE_INSENSITIVE);
        return collection.find(Filters.regex("name", namePattern)).first();
    }

    private static UUID parseUuid(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    static final class ResolvedRankedProfile {
        private final Profile profile;
        private final String displayName;

        private ResolvedRankedProfile(Profile profile, String displayName) {
            this.profile = profile;
            this.displayName = displayName;
        }

        Profile getProfile() {
            return profile;
        }

        String getDisplayName() {
            return displayName;
        }
    }
}
