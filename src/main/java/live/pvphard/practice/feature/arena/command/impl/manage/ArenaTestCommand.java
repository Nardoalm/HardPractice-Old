package live.pvphard.practice.feature.arena.command.impl.manage;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author Remi
 * @project hard-practice
 * @date 20/06/2025
 */
public class ArenaTestCommand extends BaseCommand {
    @CommandData(
            name = "arena.test",
            isAdminOnly = true,
            usage = "arena test",
            description = "Testa várias propriedades da arena para depuração"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage("Mundo: " + player.getWorld());
        player.sendMessage("Localização: " + player.getLocation());

        ArenaService arenaService = this.plugin.getService(ArenaService.class);
        player.sendMessage("Arenas copiadas: " + arenaService.getTemporaryArenas().size());

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null) {
            player.sendMessage("Arena da Partida: " + match.getArena());

            StandAloneArena arena = (StandAloneArena) match.getArena();
            if (arena != null) {
                player.sendMessage("Nome da Arena: " + arena.getName());
                player.sendMessage("Tipo da Arena: " + arena.getType());
                player.sendMessage("Posições da Arena: " + arena.getPos1() + " - " + arena.getPos2());
                player.sendMessage("Nome de Exibição da Arena: " + arena.getDisplayName());
                player.sendMessage("Mundo da Arena: " + Objects.requireNonNull(arena.getMinimum().getWorld()).getName());
                player.sendMessage("É cópia temporária: " + arena.isTemporaryCopy());
                player.sendMessage("Centro da Arena: " + arena.getCenter());
                player.sendMessage("Arena ativada: " + arena.isEnabled());
                arena.verifyArenaExists();
            } else {
                player.sendMessage("Nenhuma arena encontrada para esta partida.");
            }
        } else {
            player.sendMessage("Nenhuma partida encontrada para este perfil.");
        }
    }
}
