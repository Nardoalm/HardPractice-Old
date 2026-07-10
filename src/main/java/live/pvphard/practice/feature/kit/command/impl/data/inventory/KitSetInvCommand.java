package live.pvphard.practice.feature.kit.command.impl.data.inventory;

import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSetInvCommand extends BaseCommand {
    @CommandData(
            name = "kit.setinventory",
            aliases = "kit.setinv",
            isAdminOnly = true,
            usage = "kit setinventory <kitName>",
            description = "Sets the inventory and armor of a kit to your current inventory and armor."
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
        if (kitService == null) {
            player.sendMessage(CC.translate("&cServico de kit indisponivel no momento."));
            return;
        }
        Kit kit = kitService.getKit(args[0]);
        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        ItemStack[] inventory = InventoryUtil.cloneItemStackArray(player.getInventory().getContents());
        ItemStack[] armor = InventoryUtil.cloneItemStackArray(player.getInventory().getArmorContents());
        if (inventory == null) {
            inventory = new ItemStack[player.getInventory().getSize()];
        }
        if (armor == null) {
            armor = new ItemStack[4];
        }

        kit.setItems(inventory);
        kit.setArmor(armor);
        kitService.saveKit(kit);

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        if (profileService != null) {
            profileService.resetLayoutForKit(kit);
        }

        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_INVENTORY_SET).replace("{kit-name}", kit.getName())));
    }
}