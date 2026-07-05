package live.pvphard.practice.feature.hologram.command;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.hologram.manager.HologramManager;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.hologram.leaderboard.KitLeaderboardHologram;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

public class HologramCommand extends BaseCommand {

    @CommandData(
            name = "leaderboardnpc",
            aliases = {"lbnpc", "hologram", "lbh"},
            isAdminOnly = true,
            usage = "leaderboardnpc <spawn|remove|update> <kit/global> <type>",
            description = "Manage practice leaderboard holograms."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args == null || args.length == 0) {
            command.sendUsage();
            player.sendMessage(CC.translate("&7Types available: RANKED, UNRANKED, WIN_STREAK, FFA"));
            return;
        }

        String action = args[0].toLowerCase();
        HologramManager hologramManager = HardPractice.getInstance().getService(HologramManager.class);

        if (action.equals("update")) {
            for (KitLeaderboardHologram holo : hologramManager.getActiveHolograms()) {
                holo.update();
            }
            hologramManager.cleanupAllLoadedChunks();
            player.sendMessage(CC.translate("&aForce updated and cleaned up all active leaderboard holograms."));
            return;
        }

        if (args.length < 3) {
            command.sendUsage();
            player.sendMessage(CC.translate("&7Types available: RANKED, UNRANKED, WIN_STREAK, FFA"));
            return;
        }

        String targetType = args[1];
        boolean isGlobal = targetType.equalsIgnoreCase("global");

        if (!isGlobal) {
            Kit kit = HardPractice.getInstance().getService(KitService.class).getKit(targetType);
            if (kit == null) {
                player.sendMessage(CC.translate("&cKit or 'global' identifier not found."));
                return;
            }
        }

        LeaderboardType type;
        try {
            type = LeaderboardType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(CC.translate("&cInvalid type."));
            return;
        }

        switch (action) {
            case "spawn":
                hologramManager.saveHologram(player.getLocation(), isGlobal ? "global" : targetType, type);
                player.sendMessage(CC.translate("&aSpawned &b" + targetType.toUpperCase() + " " + type.name() + " &aland saved to storage."));
                break;

            case "remove":
                boolean removed = hologramManager.removeHologram(targetType, type);
                if (removed) {
                    player.sendMessage(CC.translate("&cRemoved &b" + targetType.toUpperCase() + " " + type.name() + " &cfrom world and storage."));
                } else {
                    player.sendMessage(CC.translate("&cLeaderboard hologram not found in configurations."));
                }
                break;

            default:
                command.sendUsage();
                break;
        }
    }
}