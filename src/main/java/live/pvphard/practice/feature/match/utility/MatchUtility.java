package live.pvphard.practice.feature.match.utility;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.setting.types.mode.*;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class MatchUtility {
    private final HardPractice plugin = HardPractice.getInstance();

    public boolean isBeyondBounds(Location location, Profile profile) {
        Arena arena = profile.getMatch().getArena();
        Location corner1 = arena.getMinimum();
        Location corner2 = arena.getMaximum();

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        boolean withinBounds;

        if (profile.getMatch().getState() == MatchState.ENDING_MATCH
                || profile.getMatch().getKit().isSettingEnabled(KitSettingBed.class)
                || profile.getMatch().getKit().isSettingEnabled(KitSettingLives.class)
                || profile.getMatch().getKit().isSettingEnabled(KitSettingRounds.class)
                || profile.getMatch().getKit().isSettingEnabled(KitSettingStickFight.class)
                || profile.getMatch().getKit().isSettingEnabled(KitSettingCheckpoint.class)) {
            withinBounds = location.getX() >= minX && location.getX() <= maxX && location.getZ() >= minZ && location.getZ() <= maxZ;
        } else {
            withinBounds = location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ;
        }

        return !withinBounds;
    }

    public void sendMatchResult(Match match, String winnerName, String loserName, UUID winnerUuid, UUID loserUuid) {
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        List<String> format = localeService.getStringList(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_REGULAR_FORMAT);
        String winnerCommand = localeService.getString(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_REGULAR_WINNER_COMMAND).replace("{winner}", String.valueOf(winnerUuid));
        String winnerHover = localeService.getString(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_REGULAR_WINNER_HOVER).replace("{winner}", winnerName);
        String loserCommand = localeService.getString(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_REGULAR_LOSER_COMMAND).replace("{loser}", String.valueOf(loserUuid));
        String loserHover = localeService.getString(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_REGULAR_LOSER_HOVER).replace("{loser}", loserName);

        for (String line : format) {
            if (line.contains("{winner}") && line.contains("{loser}")) {
                String[] parts = line.split("\\{winner}", 2);

                if (parts.length > 1) {
                    String[] loserParts = parts[1].split("\\{loser}", 2);

                    TextComponent winnerComponent = new TextComponent(CC.translate(winnerName));
                    winnerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, winnerCommand));
                    winnerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(winnerHover)).create()));

                    TextComponent loserComponent = new TextComponent(CC.translate(loserName));
                    loserComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, loserCommand));
                    loserComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(loserHover)).create()));

                    sendCombinedSpigotMessage(match,
                            new TextComponent(CC.translate(parts[0])),
                            winnerComponent,
                            new TextComponent(CC.translate(loserParts[0])),
                            loserComponent,
                            new TextComponent(loserParts.length > 1 ? CC.translate(loserParts[1]) : "")
                    );
                }
            } else if (line.contains("{winner}")) {
                String[] parts = line.split("\\{winner}", 2);

                TextComponent winnerComponent = new TextComponent(CC.translate(winnerName));
                winnerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, winnerCommand));
                winnerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(winnerHover)).create()));

                sendCombinedSpigotMessage(match,
                        new TextComponent(CC.translate(parts[0])),
                        winnerComponent,
                        new TextComponent(parts.length > 1 ? CC.translate(parts[1]) : "")
                );
            } else if (line.contains("{loser}")) {
                String[] parts = line.split("\\{loser}", 2);

                TextComponent loserComponent = new TextComponent(CC.translate(loserName));
                loserComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, loserCommand));
                loserComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(loserHover)).create()));

                sendCombinedSpigotMessage(match,
                        new TextComponent(CC.translate(parts[0])),
                        loserComponent,
                        new TextComponent(parts.length > 1 ? CC.translate(parts[1]) : "")
                );
            } else {
                match.sendMessage(CC.translate(line));
            }
        }
    }

    public void sendConjoinedMatchResult(Match match, GameParticipant<MatchGamePlayer> winnerParticipant, GameParticipant<MatchGamePlayer> loserParticipant) {
        String winnerTeamName = PlayerDisplayUtil.resolveTagColoredNick(
                winnerParticipant.getLeader().getTeamPlayer(),
                winnerParticipant.getLeader().getUsername()
        );
        String loserTeamName = PlayerDisplayUtil.resolveTagColoredNick(
                loserParticipant.getLeader().getTeamPlayer(),
                loserParticipant.getLeader().getUsername()
        );

        match.sendMessage("");
        match.sendMessage(CC.translate("&aWinning Team: &f" + winnerTeamName));

        for (MatchGamePlayer player : winnerParticipant.getAllPlayers()) {
            String commandName = player.getUsername();
            String displayName = PlayerDisplayUtil.resolveTagColoredNick(player.getTeamPlayer(), commandName);

            TextComponent playerComponent = new TextComponent(CC.translate("&7- &f" + displayName));
            playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + commandName));
            playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(CC.translate("&eClick to view " + displayName + "'s inventory")).create()));

            sendCombinedSpigotMessage(match, playerComponent);
        }

        match.sendMessage("");
        match.sendMessage(CC.translate("&cLosing Team: &f" + loserTeamName));

        for (MatchGamePlayer player : loserParticipant.getAllPlayers()) {
            String commandName = player.getUsername();
            String displayName = PlayerDisplayUtil.resolveTagColoredNick(player.getTeamPlayer(), commandName);

            TextComponent playerComponent = new TextComponent(CC.translate("&7- &f" + displayName));
            playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + commandName));
            playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(CC.translate("&eClick to view " + displayName + "'s inventory")).create()));

            sendCombinedSpigotMessage(match, playerComponent);
        }

        match.sendMessage(CC.translate(""));
    }

    public void sendCombinedSpigotMessage(Match match, BaseComponent... message) {
        match.getParticipants().forEach(gameParticipant -> {
            gameParticipant.getPlayers().forEach(uuid -> {
                Player player = plugin.getServer().getPlayer(uuid.getUuid());
                if (player != null) {
                    player.spigot().sendMessage(message);
                }
            });
        });

        match.getSpectators().forEach(uuid -> {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.spigot().sendMessage(message);
            }
        });
    }
}