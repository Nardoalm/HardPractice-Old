package live.pvphard.practice.feature.server.command.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project hard-practice
 * @since 14/07/2025
 */
public class ServiceToggleCraftingCommand extends BaseCommand {
    @CommandData(
            name = "service.togglecrafting",
            description = "Command to manage service crafting operations.",
            usage = "service togglecrafting",
            isAdminOnly = true
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInHand();
        Material itemType = (heldItem != null) ? heldItem.getType() : Material.AIR;

        ServerService serverService = this.plugin.getService(ServerService.class);
        if (itemType == null || itemType == Material.AIR || !serverService.isCraftable(itemType)) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.CRAFTING_MUST_HOLD_CRAFTABLE_ITEM));
            return;
        }

        if (serverService.getBlockedCraftingItems().contains(itemType)) {
            serverService.removeFromBlockedCraftingList(itemType);
        } else {
            serverService.addToBlockedCraftingList(itemType);
        }

        serverService.saveBlockedItems(itemType);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.CRAFTING_TOGGLED)
                .replace("{item}", itemType.name())
                .replace("{status}", serverService.getBlockedCraftingItems().contains(itemType) ? CC.translate("&cDisable") : CC.translate("&aEnable"))
        );
    }
}
