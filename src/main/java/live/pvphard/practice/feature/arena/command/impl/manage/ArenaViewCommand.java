package live.pvphard.practice.feature.arena.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.internal.types.FreeForAllArena;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Emmy
 * @project Hard
 * @date 24/09/2024 - 18:29
 */
public class ArenaViewCommand extends BaseCommand {
    @CommandData(
            name = "arena.view",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "arena view <arenaName>",
            description = "Mostra informações detalhadas sobre uma arena"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        ArenaService arenaService = this.plugin.getService(ArenaService.class);

        Arena arena = arenaService.getArenaByName(args[0]);
        if (arena == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", args[0]));
            return;
        }

        //TODO: add remaining arena details

        sender.sendMessage("");
        sender.sendMessage(CC.translate("&b&lArena " + arena.getName() + " &f(" + (arena.isEnabled() ? "&aAtivada" : "&cDesativada") + "&f)"));
        sender.sendMessage(CC.translate(" &b&l│ &rNome de Exibição: &b" + arena.getDisplayName()));
        sender.sendMessage(CC.translate(" &b&l│ &rTipo: &b" + arena.getType()));
        sender.sendMessage(CC.translate(" &b&l│ &rKits: &b" + (arena.getKits().isEmpty() ? "Nenhum" : String.join(", ", arena.getKits()))));
        sender.sendMessage(CC.translate(" &b&l│ &rEventos: &b" + (arena.getEvents().isEmpty() ? "Nenhum" : String.join(", ", arena.getEvents()))));
        if (arena instanceof FreeForAllArena) {
            FreeForAllArena ffaArena = (FreeForAllArena) arena;
            sender.sendMessage(CC.translate(" &b&l│ &rZonas Seguras:"));
            sender.sendMessage(CC.translate("   &b&l◆ &fPos1 &7(Mínimo)&f: &b" + (ffaArena.getMinimum() != null ? this.formatBlockLocation(ffaArena.getMinimum()) : "Não Definido")));
            sender.sendMessage(CC.translate("   &b&l◆ &fPos2 &7(Máximo)&f: &b" + (ffaArena.getMaximum() != null ? this.formatBlockLocation(ffaArena.getMaximum()) : "Não Definido")));
            sender.sendMessage(CC.translate(" &b&l│ &rPosições:"));
            sender.sendMessage(CC.translate("   &b&l◆ &fCentro &7(Espectador)&f: &b" + (ffaArena.getCenter() != null ? formatFullLocation(ffaArena.getCenter()) : "&cNulo")));
            sender.sendMessage(CC.translate("   &b&l◆ &fPos1: &b" + (ffaArena.getPos1() != null ? this.formatFullLocation(ffaArena.getPos1()) : "&cNulo")));
        } else {
            sender.sendMessage(CC.translate(" &b&l│ &rMínimo: &b" + (arena.getMinimum() != null ? this.formatBlockLocation(arena.getMinimum()) : "Não Definido")));
            sender.sendMessage(CC.translate(" &b&l│ &rMáximo: &b" + (arena.getMaximum() != null ? this.formatBlockLocation(arena.getMaximum()) : "Não Definido")));
            sender.sendMessage(CC.translate(" &b&l│ &rPosições:"));
            sender.sendMessage(CC.translate("   &b&l◆ &fCentro &7(Espectador)&f: &b" + (arena.getCenter() != null ? this.formatFullLocation(arena.getCenter()) : "&cNulo")));
            sender.sendMessage(CC.translate("   &b&l◆ &fAzul: &b" + (arena.getPos1() != null ? this.formatFullLocation(arena.getPos1()) : "&cNulo")));
            sender.sendMessage(CC.translate("   &b&l◆ &fVermelho: &b" + (arena.getPos2() != null ? this.formatFullLocation(arena.getPos2()) : "&cNulo")));
        }

        if (arena instanceof StandAloneArena) {
            StandAloneArena standaloneArena = (StandAloneArena) arena;
            sender.sendMessage(CC.translate(" &b&l│ &rPortais dos Times:"));
            sender.sendMessage(CC.translate("   &b&l◆ &fAzul: &b" + (standaloneArena.getTeam1Portal() != null ? this.formatBlockLocation(standaloneArena.getTeam1Portal()) : "&cNulo")));
            sender.sendMessage(CC.translate("   &b&l◆ &fVermelho: &b" + (standaloneArena.getTeam2Portal() != null ? this.formatBlockLocation(standaloneArena.getTeam2Portal()) : "&cNulo")));
            sender.sendMessage(CC.translate(" &b&l│ &rRaio do Portal: &b" + standaloneArena.getPortalRadius()));
            sender.sendMessage(CC.translate(" &b&l│ &rLimite de Altura: &b" + standaloneArena.getHeightLimit()));
            sender.sendMessage(CC.translate(" &b&l│ &rNível do Void: &b" + standaloneArena.getVoidLevel()));
        }

        sender.sendMessage("");
    }

    private String formatBlockLocation(Location loc) {
        if (loc == null) return "Não Definido";

        return String.format(Locale.ENGLISH, "%.1f, %.1f, %.1f &7[%s]",
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                Objects.requireNonNull(loc.getWorld()).getName()
        );
    }

    private String formatFullLocation(Location loc) {
        if (loc == null) return "&cNulo";

        return String.format(Locale.ENGLISH, "%.1f, %.1f, %.1f, &7%.0f, %.0f &7[%s]",
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                Objects.requireNonNull(loc.getWorld()).getName()
        );
    }
}
