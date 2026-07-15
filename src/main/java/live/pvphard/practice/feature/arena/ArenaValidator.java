package live.pvphard.practice.feature.arena;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingBridges;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRounds;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project hard-practice
 * @since 07/09/2025
 */
public class ArenaValidator {
    /**
     * Validates if an arena is fully configured before enabling or disabling it.
     * Sends appropriate messages to the player if validation fails.
     *
     * @param player the player attempting to enable/disable the arena
     * @param arena  the arena to be validated
     * @return true if the arena is fully configured, false otherwise
     */
    public boolean isEligible(Player player, Arena arena) {
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        if (arena.getMinimum() == null || arena.getMaximum() == null) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_NO_SELECTION));
            return false;
        }

        if (arena.getPos1() == null || arena.getPos2() == null) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_SPAWN_NOT_SET));
            return false;
        }

        if (arena.getCenter() == null) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_CENTER_NOT_SET));
            return false;
        }

        if (arena.getType() == ArenaType.EVENT) {
            if (arena.getEvents().isEmpty()) {
                player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_MUST_ADD_EVENT));
                return false;
            }
            return true;
        }

        if (arena.getKits().isEmpty()) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_MUST_ADD_KIT));
            return false;
        }

        KitService kitService = HardPractice.getInstance().getService(KitService.class);
        for (String kitName : arena.getKits()) {
            Kit kit = kitService.getKit(kitName);
            if (kit == null) {
                String message = localeService.getString(GlobalMessagesLocaleImpl.ARENA_ASSIGNED_KIT_NULL);
                if (message != null) {
                    player.sendMessage(message.replace("{kit-name}", kitName));
                } else {
                    player.sendMessage("§cKit not found: " + kitName);
                }
                return false;
            }

            if (arena.getType() == ArenaType.STANDALONE) {
                StandAloneArena standAloneArena = (StandAloneArena) arena;
                if ((kit.isSettingEnabled(KitSettingRounds.class) || kit.isSettingEnabled(KitSettingBridges.class)) && (standAloneArena.getTeam1Portal() == null || standAloneArena.getTeam2Portal() == null)) {
                    player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ARENA_STANDALONE_PORTALS_NOT_SET));
                    return false;
                }
            }
        }

        return true;
    }
}
