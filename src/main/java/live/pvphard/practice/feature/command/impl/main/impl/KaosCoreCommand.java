package live.pvphard.practice.feature.command.impl.main.impl;

import live.pvphard.practice.adapter.core.Core;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.adapter.core.CoreType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.common.text.CC;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
public class KaosCoreCommand extends BaseCommand {
    @CommandData(
            name = "kaos.core",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kaos core",
            description = "Exibe informações sobre a integração do core."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        Core core = this.plugin.getService(CoreAdapter.class).getCore();

        Arrays.asList(
                "",
                "&b&lInformacoes do Core Hook",
                " &b&l│ &rPlugin: &b" + core.getType().getPluginName(),
                " &b&l│ &rVersion: &b" + core.getType().getPluginVersion(),
                ""
        ).forEach(line -> sender.sendMessage(CC.translate(line)));

        if (core.getType() == CoreType.DEFAULT) {
            sender.sendMessage(CC.translate("&7Aviso: esta é a implementação padrão do servidor, pois nenhum core compatível foi encontrado."));
        }
    }
}
