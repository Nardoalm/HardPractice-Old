package live.pvphard.practice.feature.item.command.impl;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.item.ItemService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project hard-practice
 * @since 18/07/2025
 */
public class CustomItemsGoldenHeadCommand extends BaseCommand {
    @CommandData(
            name = "customitems.goldenhead",
            aliases = {"kaositems.goldenhead", "specialitems.goldenhead"},
            usage = "customitems goldenhead <amount>",
            description = "Gives the player a specific amount of custom golden heads",
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

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[0]));
            return;
        }

        if (amount <= 0) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_AMOUNT_MUST_BE_GREATER_THAN_ZERO));
            return;
        }

        ItemService itemService = this.plugin.getService(ItemService.class);
        ItemStack goldenHead = itemService.getGoldenHead();
        if (goldenHead == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ITEM_NOT_CONFIGURED).replace("{item-name}", "Custom Golden Head"));
            return;
        }

        goldenHead.setAmount(amount);
        player.getInventory().addItem(goldenHead);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ITEM_GIVEN)
                .replace("{item-name}", "Custom Golden Head" + (amount > 1 ? "s" : ""))
                .replace("{amount}", String.valueOf(amount))
        );
    }
}