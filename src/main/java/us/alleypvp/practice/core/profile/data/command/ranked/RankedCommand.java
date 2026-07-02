package us.alleypvp.practice.core.profile.data.command.ranked;

import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.library.command.BaseCommand;
import us.alleypvp.practice.library.command.CommandArgs;
import us.alleypvp.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Alley
 * @since 13/03/2025
 */
public class RankedCommand extends BaseCommand {

    //TODO: Menu for managing ranked bans? reason? duration?

    @CommandData(
            name = "ranked",
            isAdminOnly = true,
            usage = "ranked",
            description = "Gerencia a permissão de ranked."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Arrays.asList(
                " ",
                "&b&lRanked Commands Help:",
                " &f◆ &b/ranked ban &8(&7player&8) &7| Bane um jogador das partidas ranked.",
                " &f◆ &b/ranked menu &8(&7player&8) &7| Abre o menu de partidas ranked.",
                " &f◆ &b/ranked unban &8(&7player&8) &7| Desbane um jogador das partidas ranked.",
                " &f◆ &b/ranked banlist &7| Lista todos os jogadores banidos do ranked.",
                " "
        ).forEach(message -> player.sendMessage(CC.translate(message)));
    }
}
