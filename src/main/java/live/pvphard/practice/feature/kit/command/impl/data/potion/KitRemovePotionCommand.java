package live.pvphard.practice.feature.kit.command.impl.data.potion;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.menu.KitPotionListMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 16/06/2025
 */
public class KitRemovePotionCommand extends BaseCommand {
    @CommandData(
            name = "kit.removepotion",
            isAdminOnly = true,
            usage = "kit removepotion <kitName>",
            description = "Remover um efeito de poção de um kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(kitName);
        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)).replace("{kit}", kitName));
            return;
        }

        new KitPotionListMenu(kit).openMenu(player);
    }
}