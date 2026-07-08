package live.pvphard.practice.feature.kit.command.impl.manage;

import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.ActionBarReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @date 20/05/2024 - 13:06
 */
public class KitCreateCommand extends BaseCommand {
    @CommandData(
            name = "kit.create",
            isAdminOnly = true,
            usage = "kit create <kitName>",
            description = "Creates a kit with your current inventory and armor."
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
        if (kitService.getKit(kitName) != null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_ALREADY_EXISTS));
            return;
        }

        ItemStack[] inventory = InventoryUtil.cloneItemStackArray(player.getInventory().getContents());
        ItemStack[] armor = InventoryUtil.cloneItemStackArray(player.getInventory().getArmorContents());

        Material icon = Material.DIAMOND_SWORD;
        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            icon = player.getItemInHand().getType();
        }

        kitService.createKit(kitName, inventory, armor, icon);

        String message = this.getString(GlobalMessagesLocaleImpl.KIT_CREATED).replace("{kit-name}", kitName);

        player.sendMessage(message);
        this.plugin.getService(ReflectionService.class).getReflectionService(ActionBarReflectionServiceImpl.class).sendMessage(player, message, 5);
        player.sendMessage("");
        player.sendMessage(CC.translate("&7Don't forget to reload queues &c&lAFTER ENABLING &7the kit &8(/kit toggle) &7using &c&l/queue reload&7."));
        player.sendMessage("");
    }
}