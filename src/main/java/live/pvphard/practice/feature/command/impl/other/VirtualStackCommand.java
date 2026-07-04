package live.pvphard.practice.feature.command.impl.other;

import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.VirtualStackReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 14/07/2025
 */
public class VirtualStackCommand extends BaseCommand {
    @CommandData(
            name = "virtualstack",
            isAdminOnly = true,
            description = "Ignora o limite de pilha dos itens e define uma quantidade virtual (máx. 127)",
            usage = "virtualstack <amount> [bypassLimit]"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            player.sendMessage(CC.translate("&7Exemplo: /virtualstack 127"));
            player.sendMessage(CC.translate("&7Para ignorar o limite de stack, use: '/virtualstack 130 true'"));
            return;
        }

        if (player.getInventory().getItemInHand() == null || player.getInventory().getItemInHand().getType() == Material.AIR) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[0]));
            return;
        }

        boolean bypassLimit = args.length > 1 && args[1].equalsIgnoreCase("true");

        if (!bypassLimit && (amount < 1 || amount > 127)) {
            player.sendMessage(CC.translate("&cA quantidade deve estar entre 1 e 127."));
            return;
        }

        try {
            this.plugin.getService(ReflectionService.class).getReflectionService(VirtualStackReflectionServiceImpl.class).setVirtualStackAmount(player, amount);
            player.sendMessage(CC.translate("&aDefiniu a quantidade da pilha virtual para &b" + amount + "&a."));
        } catch (Exception exception) {
            Logger.logException("Falha ao definir a quantidade da pilha virtual", exception);
        }
    }
}
