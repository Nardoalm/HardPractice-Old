package live.pvphard.practice.core.profile.data.command.ranked.impl;

import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Lists players banned from ranked with safe null/exception handling.
 */
public class RankedBanListCommand extends BaseCommand {
    private static final int PAGE_SIZE = 10;

    @CommandData(
            name = "ranked.banlist",
            isAdminOnly = true,
            usage = "ranked banlist [page]",
            description = "Lista jogadores banidos das partidas ranked."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player sender = command.getPlayer();
        if (sender == null) {
            return;
        }

        int page = 1;
        String[] args = command.getArgs();
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(CC.translate("&cPágina inválida. Use um número."));
                return;
            }
        }

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        if (profileService == null) {
            sender.sendMessage(CC.translate("&cServiço de perfil indisponível no momento."));
            return;
        }

        Map<String, RankedBanEntry> bannedEntries = new LinkedHashMap<>();

        MongoCollection<Document> collection = profileService.getCollection();
        if (collection != null) {
            try {
                for (Document document : collection.find(Filters.eq("profileData.rankedBanned", true))) {
                    if (document == null) {
                        continue;
                    }

                    String name = safeString(document.getString("name"));
                    UUID uuid = parseUuid(document.getString("uuid"));

                    if (name.isEmpty() && uuid != null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        if (offlinePlayer != null && offlinePlayer.getName() != null) {
                            name = offlinePlayer.getName();
                        }
                    }

                    if (name.isEmpty()) {
                        name = uuid != null ? uuid.toString() : "Unknown";
                    }

                    addEntry(bannedEntries, new RankedBanEntry(name, uuid));
                }
            } catch (Exception exception) {
                Logger.logException("Falha ao consultar a lista de banidos do ranked no Mongo, usando fallback em cache.", exception);
            }
        }

        for (Profile profile : profileService.getProfiles().values()) {
            if (profile == null || profile.getProfileData() == null || !profile.getProfileData().isRankedBanned()) {
                continue;
            }

            String name = safeString(profile.getName());
            if (name.isEmpty()) {
                name = profile.getUuid() != null ? profile.getUuid().toString() : "Unknown";
            }

            addEntry(bannedEntries, new RankedBanEntry(name, profile.getUuid()));
        }

        List<RankedBanEntry> entries = new ArrayList<>(bannedEntries.values());
        entries.sort(Comparator.comparing(RankedBanEntry::getName, String.CASE_INSENSITIVE_ORDER));

        if (entries.isEmpty()) {
            sender.sendMessage(CC.translate("&aNão há jogadores banidos do ranked."));
            return;
        }

        int maxPages = Math.max(1, (int) Math.ceil((double) entries.size() / PAGE_SIZE));
        if (page < 1 || page > maxPages) {
            sender.sendMessage(CC.translate("&cPágina inválida. Use entre 1 e " + maxPages + "."));
            return;
        }

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, entries.size());

        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lRanked Banlist &7(" + page + "/" + maxPages + ") &8- &fTotal: &b" + entries.size()));
        for (int index = start; index < end; index++) {
            RankedBanEntry entry = entries.get(index);
            String uuidDisplay = entry.getUuid() != null ? entry.getUuid().toString() : "N/D";
            sender.sendMessage(CC.translate(" &8- &c" + entry.getName() + " &8(&7" + uuidDisplay + "&8)"));
        }
        sender.sendMessage(CC.translate(" "));
    }

    private void addEntry(Map<String, RankedBanEntry> entries, RankedBanEntry entry) {
        if (entry == null) {
            return;
        }

        String key;
        if (entry.getUuid() != null) {
            key = entry.getUuid().toString();
        } else {
            key = entry.getName().toLowerCase(Locale.ROOT);
        }

        entries.putIfAbsent(key, entry);
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    private UUID parseUuid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static final class RankedBanEntry {
        private final String name;
        private final UUID uuid;

        private RankedBanEntry(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
