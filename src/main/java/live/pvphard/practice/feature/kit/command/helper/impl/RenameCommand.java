package live.pvphard.practice.feature.kit.command.helper.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project hard-practice
 * @date 28/05/2024 - 20:16
 */
public class RenameCommand extends BaseCommand {
    @CommandData(
            name = "rename",
            isAdminOnly = true,
            usage = "rename <name>",
            description = "Renomeia o item na sua mão"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (command.getArgs().length == 0) {
            command.sendUsage();
            return;
        }

        String itemRename = String.join(" ", command.getArgs());

        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            player.sendMessage(CC.translate("&cFalha ao renomear o item."));
            return;
        }

        String originalName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : translate(itemStack.getType().name());

        itemMeta.setDisplayName(CC.translate(itemRename));
        itemStack.setItemMeta(itemMeta);

        player.updateInventory();

        player.sendMessage(CC.translate("&aRenamed &f" + originalName + " &ato &f" + itemRename));
    }

    private String translate(String name) {
        return Arrays.stream(name.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
