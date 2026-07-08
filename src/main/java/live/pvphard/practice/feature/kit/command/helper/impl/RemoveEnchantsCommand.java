package live.pvphard.practice.feature.kit.command.helper.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 28/10/2024 - 09:15
 */
public class RemoveEnchantsCommand extends BaseCommand {
    @CommandData(
            name = "removeenchants",
            aliases = "enchantsremovement",
            isAdminOnly = true,
            usage = "removeenchants",
            description = "Removes all enchantments from the item you are holding."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (player.getInventory().getItemInHand() == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        if (player.getInventory().getItemInHand().getEnchantments().isEmpty()) {
            player.sendMessage(CC.translate("&cThe item you are holding does not have any enchantments."));
            return;
        }

        player.sendMessage(CC.translate("&cEnchantments: &f" + player.getInventory().getItemInHand().getEnchantments().keySet()));

        player.getInventory().getItemInHand().getEnchantments().keySet().forEach(enchant -> {
            player.getInventory().getItemInHand().removeEnchantment(enchant);
        });

        player.sendMessage(CC.translate("&aRemoved all enchantments from item &b" + player.getInventory().getItemInHand().getType().name() + "&a."));
    }
}
