package live.pvphard.practice.feature.kit.command.impl.data;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class KitSetIconCommand extends BaseCommand {
    @CommandData(
            name = "kit.seticon",
            isAdminOnly = true,
            usage = "kit seticon <kitName>",
            description = "Define o ícone de um kit para o item em sua mão."
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
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        kit.setIcon(player.getItemInHand().getType());
        kit.setDurability(player.getItemInHand().getDurability());
        kitService.saveKit(kit);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_ICON_SET)
                .replace("{kit-name}", kit.getName())
                .replace("{icon}", player.getItemInHand().getType().name().toUpperCase() + ":" + player.getItemInHand().getDurability())
        );
    }
}
