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
            description = "Fornece assistência para comandos utilitários."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Arrays.asList(
                "",
                "&b&lAjuda dos Comandos Auxiliares de Kit:",
                " &f◆ &b/enchant &8(&7enchantment&8) &8(&7level&8) &7| &fEncanta o item na mão.",
                " &f◆ &b/glow &8(&7true|false&8) &7| &fDefine o glow do item.",
                " &f◆ &b/potionduration &8(&7duration&8) &7| &fDefine a duração de uma poção.",
                " &f◆ &b/removeenchants &7| &fRemove os encantamentos do item.",
                " &f◆ &b/rename &8(&7name&8) &7| &fRenomeia o item na mão.",
                " &f◆ &b/unbreakable &8(&7true|false&8) &7| &fDefine o item como inquebrável.",
                ""
        ).forEach(message -> command.getSender().sendMessage(CC.translate(message)));
    }
}
