package live.pvphard.practice.feature.match.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.pagination.PaginatedMenu;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.internal.types.DefaultMatch;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.menu.statistic.StatisticsMenu;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.reflect.utility.ReflectionUtility;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Emmy
 * @project Hard
 * @since 26/06/2025
 */
@AllArgsConstructor
public class SpectatorTeleportMenu extends PaginatedMenu {
    private final FileConfiguration config = HardPractice.getInstance().getService(ConfigService.class).getMenusConfig();
    private final String path = "menus.spectator-teleport";

    private final Match match;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.config.getString(this.path + ".title", "&cERROR");

    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        this.addGlassHeader(buttons, this.config.getInt(this.path + ".buttons.match-info-button.glass-durability", 15));
        buttons.put(this.config.getInt(this.path + ".buttons.match-info-button.slot", 4), new MatchInfoButton(this.match));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<GameParticipant<MatchGamePlayer>> participants = this.match.getParticipants();

        participants.forEach(participant ->
                participant.getPlayers().forEach(gamePlayer ->
                        buttons.put(buttons.size(), new SpectatorTeleportButton(gamePlayer))
                )
        );

        return buttons;
    }

    @AllArgsConstructor
    private static class SpectatorTeleportButton extends Button {
        private final FileConfiguration config = HardPractice.getInstance().getService(ConfigService.class).getMenusConfig();
        private final String path = "menus.spectator-teleport";

        private final MatchGamePlayer gamePlayer;

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(this.gamePlayer.getUuid());
            DefaultMatch match = (DefaultMatch) profile.getMatch();

            List<String> lore = new ArrayList<>();
            List<String> configLore = this.config.getStringList(this.path + ".buttons.spectator-teleport-button.lore");
            if (configLore.isEmpty()) {
                configLore = Collections.singletonList(
                        "&cSomething went wrong with your config."
                );
            }

            int ping = ReflectionUtility.getPing(this.gamePlayer.getTeamPlayer());
            ChatColor team = match.getTeamColor(match.getParticipant(this.gamePlayer.getTeamPlayer()));
            String teamColor = team.name();
            int elo = match.isRanked() ? profile.getProfileData().getRankedKitData().get(match.getKit().getName()).getElo() : -1;

            for (String line : configLore) {
                lore.add(line
                        .replace("{player-color}", String.valueOf(profile.getNameColor()))
                        .replace("{username}", this.gamePlayer.getUsername())
                        .replace("{ping}", String.valueOf(ping))
                        .replace("{team}", team + teamColor)
                        .replace("{elo}", String.valueOf(elo))
                );
            }

            String name = this.config.getString(this.path + ".buttons.spectator-teleport-button.name", "&b{username}")
                    .replace("{username}", this.gamePlayer.getUsername())
                    .replace("{player-color}", String.valueOf(profile.getNameColor())
                    );

            return new ItemBuilder(Material.SKULL_ITEM)
                    .setSkull(this.gamePlayer.getUsername())
                    .name(name)
                    .lore(lore)
                    .durability(3)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.LEFT) {
                if (this.gamePlayer.isDead() || this.gamePlayer.isDisconnected()) {
                    player.sendMessage(CC.translate("&cEste jogador está morto ou desconectado."));
                    return;
                }
                player.teleport(this.gamePlayer.getTeamPlayer().getLocation());
            } else if (clickType == ClickType.RIGHT) {
                new StatisticsMenu(this.gamePlayer.getTeamPlayer()).openMenu(player);
            }
        }
    }

    @AllArgsConstructor
    private static class MatchInfoButton extends Button {
        private final Match match;

        @Override
        public ItemStack getButtonItem(Player player) {
            int playerParticipantSize = this.match.getParticipants().stream()
                    .mapToInt(participant -> participant.getPlayers().size())
                    .sum();

            FileConfiguration config = HardPractice.getInstance().getService(ConfigService.class).getMenusConfig();
            String path = "menus.spectator-teleport.buttons.match-info-button";
            String name = config.getString(path + ".name", "&b&lInformações da Partida");
            List<String> lore = config.getStringList(path + ".lore");
            if (lore.isEmpty()) {
                lore = Collections.singletonList(
                        "&cAlgo deu errado com a sua configuração."
                );
            }

            List<String> replacedLore = new ArrayList<>();
            for (String line : lore) {
                replacedLore.add(line
                        .replace("{kit}", this.match.getKit().getName())
                        .replace("{arena}", this.match.getArena().getName())
                        .replace("{players}", String.valueOf(playerParticipantSize))
                );
            }

            return new ItemBuilder(Material.PAPER)
                    .name(name)
                    .lore(replacedLore)
                    .hideMeta()
                    .build();
        }
    }
}
