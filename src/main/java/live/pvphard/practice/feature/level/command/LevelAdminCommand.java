package live.pvphard.practice.feature.level.command;

import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.library.command.annotation.CompleterData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 26/05/2025
 */
public class LevelAdminCommand extends BaseCommand {
    @CompleterData(name = "leveladmin")
    public List<String> kitCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();
        if (command.getArgs().length != 1) {
            return completion;
        }

        if (!command.getPlayer().hasPermission(this.plugin.getService(PluginConstant.class).getAdminPermissionPrefix())) {
            return completion;
        }

        completion.addAll(Arrays.asList(
                "create", "delete", "view", "setminelo",
                "setmaxelo", "setdisplayname", "seticon", "list"
        ));

        return completion;
    }

    @CommandData(
            name = "leveladmin",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "leveladmin",
            description = "Comando administrativo para gerenciar níveis"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Arrays.asList(
                "",
                "&b&lAjuda dos Comandos Administrativos de Nível:",
                " &f◆ &b/leveladmin create &8(&7levelName&8) &8(&7minElo&8) &8(&7maxElo&8) &7| Cria um novo nível",
                " &f◆ &b/leveladmin delete &8(&7levelName&8) &7| Exclui um nível",
                " &f◆ &b/leveladmin list &7| Lista todos os níveis",
                " &f◆ &b/leveladmin view &8(&7levelName&8) &7| Mostra as informações de um nível",
                " &f◆ &b/leveladmin setminelo &8(&7levelName&8) &8(&7minElo&8) &7| Define o Elo mínimo de um nível",
                " &f◆ &b/leveladmin setmaxelo &8(&7levelName&8) &8(&7maxElo&8) &7| Define o Elo máximo de um nível",
                " &f◆ &b/leveladmin setdisplayname &8(&7levelName&8) &8(&7displayName&8) &7| Define o nome de exibição de um nível",
                " &f◆ &b/leveladmin seticon &8(&7levelName&8) &7| Define o material de um nível",
                ""
        ).forEach(line -> command.getSender().sendMessage(CC.translate(line)));
    }
}
