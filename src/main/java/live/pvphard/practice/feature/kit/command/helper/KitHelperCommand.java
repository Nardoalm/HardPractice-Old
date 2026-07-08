package live.pvphard.practice.feature.kit.command.helper;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;

import java.util.Arrays;

/**
 * @author Emmy
 * @project hard-practice
 * @since 14/07/2025
 */
public class KitHelperCommand extends BaseCommand {
    @CommandData(
            name = "kithelper",
            isAdminOnly = true,
            usage = "kithelper",
            description = "Provides assistance for utility commands."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Arrays.asList(
                "",
                "&b&lKit Utility Commands Help:",
                " &f◆ &b/enchant &8(&7enchantment&8) &8(&7level&8) &7| &fEnchants the item in hand.",
                " &f◆ &b/glow &8(&7true|false&8) &7| &fSets the item glow.",
                " &f◆ &b/potionduration &8(&7duration&8) &7| &fSets the duration of a potion.",
                " &f◆ &b/removeenchants &7| &fRemoves all enchantments from the item.",
                " &f◆ &b/rename &8(&7name&8) &7| &fRenames the item in hand.",
                " &f◆ &b/unbreakable &8(&7true|false&8) &7| &fSets the item as unbreakable.",
                ""
        ).forEach(message -> command.getSender().sendMessage(CC.translate(message)));
    }
}
