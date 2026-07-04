package live.pvphard.practice.feature.kit.command.impl.data.inventory;

import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @date 28/04/2024 - 22:25
 */
public class KitGetInvCommand extends BaseCommand {
    @CommandData(
            name = "kit.getinventory",
            aliases = "kit.getinv",
            isAdminOnly = true,
            usage = "kit getinventory <kitName>",
            description = "Obtém o inventário de um kit"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (command.length() < 1) {
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

        ItemStack[] items = InventoryUtil.cloneItemStackArray(kit.getItems());
        ItemStack[] armor = InventoryUtil.cloneItemStackArray(kit.getArmor());

        player.getInventory().setContents(items != null ? items : new ItemStack[player.getInventory().getSize()]);
        player.getInventory().setArmorContents(armor != null ? armor : new ItemStack[4]);
        player.updateInventory();
        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_INVENTORY_GIVEN).replace("{kit-name}", kit.getName())));
    }
}
