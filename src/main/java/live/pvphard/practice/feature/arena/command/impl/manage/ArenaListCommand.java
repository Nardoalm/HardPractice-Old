package live.pvphard.practice.feature.arena.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.LoreHelper;
import live.pvphard.practice.common.text.TextFormatter;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

/**
 * @author Emmy
 * @project Hard
 * @date 10/07/2025
 */
public class ArenaListCommand extends BaseCommand {
    @CommandData(
            name = "arena.list",
            aliases = {"arenas"},
            isAdminOnly = true,
            usage = "arena list",
            description = "Lista todas as arenas no servidor"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ArenaService arenaService = this.plugin.getService(ArenaService.class);

        player.sendMessage("");
        player.sendMessage(CC.translate("     &b&lLista de Arenas &f(" + arenaService.getArenas().size() + "&f)"));

        if (arenaService.getArenas().isEmpty()) {
            player.sendMessage(CC.translate("      &f◆ &cNenhuma arena disponível."));
            return;
        }

        arenaService.getArenas().stream()
                .sorted(Comparator.comparing(Arena::getName))
                .forEach(arena -> {
                    ComponentBuilder hover = new ComponentBuilder("")
                            .append(CC.translate("&b&lInformações da Arena" + LoreHelper.displayEnabled(arena.isEnabled()) + "\n"))
                            .append(CC.translate(" &f● Nome de Exibição: &b" + arena.getDisplayName() + "\n"))
                            .append(CC.translate(" &f● Tipo: &b" + arena.getType().name() + "\n"))
                            .append(CC.translate(" &f● Eventos: &b" + (arena.getEvents().isEmpty() ? "Nenhum" : String.join(", ", arena.getEvents())) + "\n"))
                            .append(CC.translate(" &f● Centro: &b" + TextFormatter.formatLocation(arena.getCenter()) + "\n"))
                            .append(CC.translate(" &f● Pos1: &b" + TextFormatter.formatLocation(arena.getPos1()) + "\n"))
                            .append(CC.translate(" &f● Pos2: &b" + TextFormatter.formatLocation(arena.getPos2()) + "\n"));

                    ComponentBuilder message = new ComponentBuilder("      ");
                    message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));

                    message.append("● ").color(ChatColor.WHITE.asBungee());
                    message.append(arena.getName()).color(ChatColor.GOLD.asBungee());
                    message.append(" - ").color(ChatColor.WHITE.asBungee());

                    message.append("[TP] ")
                            .color(ChatColor.GREEN.asBungee())
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena teleport " + arena.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Clique para teleportar até " + arena.getName()).color(ChatColor.GREEN.asBungee()).create()));

                    message.append("[X] ")
                            .color(ChatColor.RED.asBungee())
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/arena delete " + arena.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Clique para excluir " + arena.getName()).color(ChatColor.RED.asBungee()).create()));

                    message.append("[i]")
                            .color(ChatColor.GRAY.asBungee())
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena view " + arena.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Clique para ver informações detalhadas de " + arena.getName()).color(ChatColor.GRAY.asBungee()).create()));

                    player.spigot().sendMessage(message.create());
                });

        player.sendMessage("");
    }
}
