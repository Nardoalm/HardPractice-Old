package live.pvphard.practice.feature.ffa.command.impl.admin.manage;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 11/04/2025
 */
public class FFAToggleCommand extends BaseCommand {
    @CommandData(
            name = "ffa.toggle",
            isAdminOnly = true,
            usage = "ffa toggle <kitName>",
            description = "Alterna a elegibilidade de um kit para partidas de FFA."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(args[0]);
        if (kit == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND).replace("{kit-name}", args[0]));
            return;
        }

        FFAService ffaService = this.plugin.getService(FFAService.class);
        if (ffaService.isNotEligibleForFFA(kit)) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_KIT_NOT_ELIGIBLE));
            return;
        }

        kit.setFfaEnabled(!kit.isFfaEnabled());
        boolean ffaEnabled = kit.isFfaEnabled();

        kitService.saveKit(kit);
        ffaService.reloadFFAKits();
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_TOGGLED)
                .replace("{kit-name}", kit.getName())
                .replace("{status}", ffaEnabled ? "enable" : "disable")
        );
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_KITS_RELOADED));
    }
}
