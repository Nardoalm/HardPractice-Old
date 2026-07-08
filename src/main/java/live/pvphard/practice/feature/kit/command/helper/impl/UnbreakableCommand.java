package live.pvphard.practice.feature.kit.command.helper.impl;

import live.pvphard.practice.common.reflect.utility.ReflectionUtility;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @date 14/07/2025
 */
public class UnbreakableCommand extends BaseCommand {
    @CommandData(
            name = "unbreakable",
            description = "Sets the unbreakable status of the item in your hand",
            usage = "unbreakable <true|false>",
            isAdminOnly = true
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        boolean unbreakable = Boolean.parseBoolean(args[0]);

        ItemStack unbreakAbleItem = ReflectionUtility.setUnbreakable(item, unbreakable);
        player.setItemInHand(unbreakAbleItem);

        player.sendMessage(CC.translate("&aSet the unbreakable status of the item to &b" + unbreakable + "&a."));
    }
}
