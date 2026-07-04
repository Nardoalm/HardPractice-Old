package live.pvphard.practice.core.locale.internal.impl.message;

import live.pvphard.practice.core.locale.LocaleEntry;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;

@Getter
public enum GameMessagesLocaleImpl implements LocaleEntry {
    DUEL_REQUEST_ACCEPTED("messages/game-messages.yml", "duel-requests.accepted", "&aYou accepted the duel request from {name-color}{player}&a!"),
    DUEL_REQUEST_SENT_SOLO("messages/game-messages.yml", "duel.request.sent.solo.format", Arrays.asList(
            "",
            "&b&lDuel Request Sent",
            " &b&l│ &fTarget: &b{name-color}{target}",
            " &b&l│ &fKit: &b{kit}",
            " &b&l│ &fArena: &b{arena}",
            ""
    )),
    DUEL_REQUEST_SENT_PARTY("messages/game-messages.yml", "duel.request.sent.party.format", Arrays.asList(
            "",
            "&b&lParty Duel Request Sent",
            " &b&l│ &fTarget: &b{name-color}{target}'s Party &b(&a{party-size}&b)",
            " &b&l│ &fKit: &b{kit}",
            " &b&l│ &fArena: &b{arena}",
            ""
    )),

    DUEL_REQUEST_RECEIVED_SOLO_CLICKABLE_FORMAT("messages/game-messages.yml", "duel.request.received.solo.clickable.format", " &a(Click to accept)"),
    DUEL_REQUEST_RECEIVED_SOLO_CLICKABLE_COMMAND("messages/game-messages.yml", "duel.request.received.solo.clickable.command", "/accept {sender}"),
    DUEL_REQUEST_RECEIVED_SOLO_CLICKABLE_HOVER("messages/game-messages.yml", "duel.request.received.solo.clickable.hover", "&aClick to accept the duel request from &b{sender}&a."),
    DUEL_REQUEST_RECEIVED_SOLO("messages/game-messages.yml", "duel.request.received.solo.format", Arrays.asList(
            "",
            "&b&lDuel Request Received",
            " &b&l│ &fFrom: &b{name-color}{sender}",
            " &b&l│ &fArena: &b{arena}",
            " &b&l│ &fKit: &b{kit}",
            "{clickable}",
            ""
    )),

    DUEL_REQUEST_RECEIVED_PARTY_CLICKABLE_FORMAT("messages/game-messages.yml", "duel.request.received.party.clickable.format", " &a(Click to accept)"),
    DUEL_REQUEST_RECEIVED_PARTY_CLICKABLE_COMMAND("messages/game-messages.yml", "duel.request.received.party.clickable.command", "/accept {sender}"),
    DUEL_REQUEST_RECEIVED_PARTY_CLICKABLE_HOVER("messages/game-messages.yml", "duel.request.received.party.clickable.hover", "&aClick to accept the party duel request from &b{sender}&a."),
    DUEL_REQUEST_RECEIVED_PARTY("messages/game-messages.yml", "duel.request.received.party.format", Arrays.asList(
            "",
            "&b&lParty Duel Request Received",
            " &b&l│ &fFrom: &b{name-color}{sender}'s Party &b(&a{party-size}&b)",
            " &b&l│ &fArena: &b{arena}",
            " &b&l│ &fKit: &b{kit}",
            "{clickable}",
            ""
    )),

    DUEL_REQUEST_EXPIRED_ENABLED_BOOLEAN("messages/game-messages.yml", "duel.request.expired.sender.enabled", true),
    ERROR_DUEL_REQUESTS_EXPIRED("messages/game-messages.yml", "duel.request.expired.sender.format", Arrays.asList(
            "",
            "&c&lDuel Request Expired",
            " &c&l│ &fYour duel request sent to &b{target} &fhas expired.",
            ""
    )),

    DUEL_REQUEST_EXPIRED_TARGET_ENABLED_BOOLEAN("messages/game-messages.yml", "duel.request.expired.target.enabled", true),
    DUEL_REQUEST_EXPIRED_TARGET("messages/game-messages.yml", "duel.request.expired.target.format", Arrays.asList(
            "",
            "&c&lDuel Request Expired",
            " &c&l│ &fThe duel request from &b{sender} &fhas expired.",
            ""
    )),

    FFA_KILLSTREAK_ALERT_ENABLED_BOOLEAN("messages/game-messages.yml", "ffa.killstreak-alert.enabled", true),
    FFA_KILLSTREAK_ALERT_INTERVAL("messages/game-messages.yml", "ffa.killstreak-alert.interval", 5),
    FFA_KILLSTREAK_ALERT_MESSAGE("messages/game-messages.yml", "ffa.killstreak-alert.message", Arrays.asList(
            "",
            "&b&lKillstreak",
            " &b&l│ &f{name-color}{player} &fis on a killstreak of &b{killstreak}&f!",
            ""
    )),

    FFA_COMBAT_LOG_DEATH_MESSAGE("messages/game-messages.yml", "ffa.combat-log.death-message", "&7(Combat Log) &c{name-color}{player} &fwas killed by &c{killer-name-color}{killer}&f."),

    FFA_TELEPORTED_TO_SAFE_ZONE("messages/game-messages.yml", "ffa.teleported-to-safe-zone", "&aYou have been teleported to the safezone."),

    FFA_PLAYER_JOIN_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "ffa.player.join-message.enabled", false),
    FFA_PLAYER_JOIN_MESSAGE_FORMAT("messages/game-messages.yml", "ffa.player.join-message.format", Collections.singletonList("&a{name-color}{player} &ajoined the FFA match.")),

    FFA_PLAYER_LEFT_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "ffa.player.left-message.enabled", false),
    FFA_PLAYER_LEFT_MESSAGE_FORMAT("messages/game-messages.yml", "ffa.player.left-message.format", Collections.singletonList("&c{name-color}{player} &cleft the FFA match.")),

    FFA_PLAYER_DIED_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "ffa.player.death.no-killer-message.enabled", true),
    FFA_PLAYER_DIED_MESSAGE_FORMAT("messages/game-messages.yml", "ffa.player.death.no-killer-message.format", Collections.singletonList("&c{name-color}{player} &fdied!")),

    FFA_PLAYER_KILLED_PLAYER_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "ffa.player.death.killed-message.enabled", true),
    FFA_PLAYER_KILLED_PLAYER_MESSAGE_FORMAT("messages/game-messages.yml", "ffa.player.death.killed-message.format", Collections.singletonList("&c{name-color}{player} &fwas killed by &c{killer-name-color}{killer}&f!")),

    GAME_CANNOT_DROP_SWORD("messages/game-messages.yml", "game.cannot-drop-sword", "&cYou cannot drop your sword!"),

    MATCH_DEATH_MESSAGE_GENERIC("messages/game-messages.yml", "match.death-message.generic", "&c&lDEATH! &f{name-color}{player} &fdied."),
    MATCH_DEATH_MESSAGE_GENERIC_KILLER("messages/game-messages.yml", "match.death-message.generic-killer", "&c&lDEATH! &f{name-color}{victim} &fwas killed by &c{killer-name-color}{killer}&f."),
    MATCH_DEATH_MESSAGE_CUSTOM("messages/game-messages.yml", "match.death-message.custom", "&c&lDEATH! &f{message}"),

    MATCH_SEEKER_RESPAWNED("messages/game-messages.yml", "match.seeker-respawned", "&c&lDEATH! &fSeeker &c{name-color}{player} &fhas respawned."),

    MATCH_STARTED_DISCLAIMER_ENABLED_BOOLEAN("messages/game-messages.yml", "match.started.kit-disclaimer.enabled", true),
    MATCH_STARTED_DISCLAIMER_FORMAT("messages/game-messages.yml", "match.started.kit-disclaimer.format", Arrays.asList(
            "",
            "&b&l{kit-name}",
            " &b&l│ &f{kit-disclaimer}",
            ""
    )),

    MATCH_ELEVATOR_NO_SAFE_SPOT_FOUND("messages/game-messages.yml", "match.elevator.no-safe-spot-found", "&cNo safe spot found to teleport!"),
    MATCH_CANNOT_INTERACT_DURING_MATCH("messages/game-messages.yml", "match.cannot-interact-during-match", "&cYou cannot interact with this during the match!"),
    MATCH_CANNOT_INTERACT_AS_RAIDER("messages/game-messages.yml", "match.cannot-interact-as-raider", "&cYou cannot interact with this as a raider!"),
    MATCH_CANNOT_PLACE_BLOCKS_DURING_HIDING_PHASE("messages/game-messages.yml", "match.cannot-place-blocks-during-hiding-phase", "&cYou cannot place blocks during the hiding phase!"),
    MATCH_CANNOT_PLACE_BLOCKS_ABOVE_HEIGHT_LIMIT("messages/game-messages.yml", "match.cannot-place-blocks-above-height-limit", "&cYou cannot place blocks above the height limit!"),
    MATCH_CANNOT_BUILD_NEAR_PORTAL("messages/game-messages.yml", "match.cannot-build-near-portal", "&cYou cannot build near a portal!"),
    MATCH_CANNOT_BREAK_BLOCKS_DURING_HIDING_PHASE("messages/game-messages.yml", "match.cannot-break-blocks-during-hiding-phase", "&cYou cannot break blocks during the hiding phase!"),
    MATCH_CANNOT_BREAK_OWN_BED("messages/game-messages.yml", "match.cannot-break-own-bed", "&cYou cannot break your own bed!"),

    MATCH_STARTED_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.started.message.regular.enabled", true),
    MATCH_STARTED_MESSAGE_FORMAT("messages/game-messages.yml", "match.started.message.regular.format", Collections.singletonList("&aThe match has started!")),

    MATCH_ROUND_STARTED_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.started.message.round.enabled", true),
    MATCH_ROUND_STARTED_MESSAGE_FORMAT("messages/game-messages.yml", "match.started.message.round.format", Collections.singletonList("&aRound &b{current-round} &ahas started.")),

    MATCH_STARTING_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.starting.message.enabled", true),
    MATCH_STARTING_MESSAGE_FORMAT("messages/game-messages.yml", "match.starting.message.format", Collections.singletonList("&a{stage}...")),

    MATCH_ROUND_STARTING_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.round-starting.message.enabled", true),
    MATCH_ROUND_STARTING_MESSAGE_FORMAT("messages/game-messages.yml", "match.round-starting.message.format", Collections.singletonList("&a{stage}...")),

    MATCH_RESPAWNING_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.respawning.message.enabled", true),
    MATCH_RESPAWNING_MESSAGE_FORMAT("messages/game-messages.yml", "match.respawning.message.format", Collections.singletonList("&a{seconds}...")),

    MATCH_TIME_LIMIT_EXCEEDED_FORMAT("messages/game-messages.yml", "match.ending.time-limit-exceeded.format", Collections.singletonList("&cThe match ended because it reached the time limit of &b{time-limit} minutes&c.")),

    MATCH_SCORED_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.scored.enabled", true),
    MATCH_SCORED_MESSAGE_SOLO_FORMAT("messages/game-messages.yml", "match.scored.format.solo", Arrays.asList(
            ""
            , "{winner-color}&l{scorer} &b&lscored!"
            , "{winner-color}&l{winner-goals} &7- {loser-color}&l{loser-goals}"
            , ""
    )),
    MATCH_SCORED_MESSAGE_TEAM_FORMAT("messages/game-messages.yml", "match.scored.format.team", Arrays.asList(
            ""
            , "{winner-color}&lTeam {winner} &b&lscored!"
            , "{winner-color}&l{winner-goals} &7- {loser-color}&l{loser-goals}"
            , ""
    )),

    MATCH_BED_DESTRUCTION_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.bed-destruction.enabled", true),
    MATCH_BED_DESTRUCTION_MESSAGE_FORMAT("messages/game-messages.yml", "match.bed-destruction.format", Arrays.asList(
            "",
            "&b&lBED DESTROYED! &b{bed-color}{bed} &fwas destroyed by &b{breaker-color}{breaker}&f!",
            ""
    )),

    MATCH_ENDED_SPECTATORS_LIST("messages/game-messages.yml", "match.ended.spectators-list", Collections.singletonList("&b&lSpectators: &f{spectators} &7(and &b{more_count} &7more...)")),
    MATCH_ENDED_MATCH_RESULT_REGULAR_FORMAT("messages/game-messages.yml", "match.ended.match-result.regular.format", Arrays.asList(
            "",
            "&b&lMatch Result: &7(click to view)",
            "&aWinner: &f{winner} &7| &cLoser: &f{loser}",
            ""
    )),
    MATCH_ENDED_MATCH_RESULT_REGULAR_WINNER_COMMAND("messages/game-messages.yml", "match.ended.match-result.regular.winner.command", "/inventory {winner}"),
    MATCH_ENDED_MATCH_RESULT_REGULAR_WINNER_HOVER("messages/game-messages.yml", "match.ended.match-result.regular.winner.hover", "&aClick to view the inventory of {winner}"),
    MATCH_ENDED_MATCH_RESULT_REGULAR_LOSER_COMMAND("messages/game-messages.yml", "match.ended.match-result.regular.loser.command", "/inventory {loser}"),
    MATCH_ENDED_MATCH_RESULT_REGULAR_LOSER_HOVER("messages/game-messages.yml", "match.ended.match-result.regular.loser.hover", "&cClick to view the inventory of {loser}"),

    MATCH_ENDED_MATCH_RESULT_ELO_CHANGES_ENABLED_BOOLEAN("messages/game-messages.yml", "match.ended.elo-changes.enabled", true),
    MATCH_ENDED_MATCH_RESULT_ELO_CHANGES_FORMAT("messages/game-messages.yml", "match.ended.elo-changes.format", Arrays.asList(
            "&b&lElo Changes",
            " &b&l│ &f{winner} &a+{math-winner-elo} &7(&f{old-winner-elo} &7-> &f{new-winner-elo})",
            " &b&l│ &f{loser} &c-{math-loser-elo} &7(&f{old-loser-elo} &7-> &f{new-loser-elo})",
            ""
    )),

    MATCH_BLOCKS_RESET_MESSAGE_ENABLED_BOOLEAN("messages/game-messages.yml", "match.blocks-reset.enabled", true),
    MATCH_BLOCKS_RESET_MESSAGE_FORMAT("messages/game-messages.yml", "match.blocks-reset.format", Collections.singletonList("&4{name-color}{player} &fhas reset the blocks!")),

    MATCH_PLATFORM_DECAY_WILL_NO_LONGER_DECAY("messages/game-messages.yml", "match.platform-decay.will-no-longer-decay", Collections.singletonList("&c&lThe platform will no longer decay!")),

    MATCH_PLATFORM_DECAY_NOTIFICATION_75_FORMAT("messages/game-messages.yml", "match.platform-decay.notifications.75.format", Collections.singletonList("&bThe arena has begun to decay...")),
    MATCH_PLATFORM_DECAY_NOTIFICATION_50_FORMAT("messages/game-messages.yml", "match.platform-decay.notifications.50.format", Collections.singletonList("&e&lWARNING! &eThe arena has shrunk by half!")),
    MATCH_PLATFORM_DECAY_NOTIFICATION_25_FORMAT("messages/game-messages.yml", "match.platform-decay.notifications.25.format", Collections.singletonList("&c&lDANGER! &cThe platform is collapsing fast!")),

    MATCH_PLAYER_VS_PLAYER_SOLO_ENABLED_BOOLEAN("messages/game-messages.yml", "match.versus-message.solo.enabled", true),
    MATCH_PLAYER_VS_PLAYER_SOLO_FORMAT("messages/game-messages.yml", "match.versus-message.solo.format", Collections.singletonList("&7[&bMatch&7] &b{playerA} &avs &b{playerB}")),

    MATCH_PLAYER_VS_PLAYER_TEAM_ENABLED_BOOLEAN("messages/game-messages.yml", "match.versus-message.team.enabled", true),
    MATCH_PLAYER_VS_PLAYER_TEAM_FORMAT("messages/game-messages.yml", "match.versus-message.team.format", Collections.singletonList("&7[&bMatch&7] &bTeam {teamA-leader} &7(&a{teamA-size}&7) &avs &bTeam {teamB-leader} &7(&a{teamB-size}&7)")),

    PARTY_INVITATION_RECEIVED_CLICKABLE_FORMAT("messages/game-messages.yml", "party.invitation.received.clickable.format", " &a(Click to join)"),
    PARTY_INVITATION_RECEIVED_CLICKABLE_COMMAND("messages/game-messages.yml", "party.invitation.received.clickable.command", "/party accept {sender}"),
    PARTY_INVITATION_RECEIVED_CLICKABLE_HOVER("messages/game-messages.yml", "party.invitation.received.clickable.hover", "&aClick to accept the party invitation from &b{sender}&a."),
    PARTY_INVITATION_RECEIVED("messages/game-messages.yml", "party.invitation.received.format", Arrays.asList(
            "",
            "&b&lParty Invitation",
            " &b&l│ &fFrom: &b{name-color}{sender}",
            " &b&l│ &fPlayers: &b{party-size}&f/&bN/A",
            "{clickable}",
            ""
    )),

    PARTY_INVITATION_SENT("messages/game-messages.yml", "party.invitation.sent.format", Arrays.asList(
            "",
            "&b&lParty Invitation Sent",
            " &b&l│ &fTarget: &b{name-color}{target}",
            ""
    )),

    PARTY_ANNOUNCEMENT_CLICKABLE_FORMAT("messages/game-messages.yml", "party.announcement.clickable.format", " &a(Click to join)"),
    PARTY_ANNOUNCEMENT_CLICKABLE_COMMAND("messages/game-messages.yml", "party.announcement.clickable.command", "/party join {leader}"),
    PARTY_ANNOUNCEMENT_CLICKABLE_HOVER("messages/game-messages.yml", "party.announcement.clickable.hover", "&aClick to join &b{leader}&a's party."),
    PARTY_ANNOUNCEMENT("messages/game-messages.yml", "party.announcement.format", Arrays.asList(
            "",
            "&b&lPublic Party Announcement",
            " &b&l│ &fLeader: &b{name-color}{leader}",
            " &b&l│ &fPlayers: &b{party-size}&f/&bN/A",
            "{clickable}",
            ""
    )),

    PARTY_REQUEST_EXPIRED_ENABLED_BOOLEAN("messages/game-messages.yml", "party.request.expired.sender.enabled", true),
    PARTY_REQUEST_EXPIRED("messages/game-messages.yml", "party.request.expired.sender.format", Arrays.asList(
            "",
            "&c&lParty Request Expired",
            " &c&l│ &fYour party request sent to &b{target} &fhas expired.",
            ""
    )),
    PARTY_REQUEST_EXPIRED_TARGET_ENABLED_BOOLEAN("messages/game-messages.yml", "party.request.expired.target.enabled", true),
    PARTY_REQUEST_EXPIRED_TARGET("messages/game-messages.yml", "party.request.expired.target.format", Arrays.asList(
            "",
            "&c&lParty Request Expired",
            " &c&l│ &fThe party request from &b{sender} &fhas expired.",
            ""
    )),

    ;

    private final String configName;
    private final String configPath;
    private final Object defaultValue;

    GameMessagesLocaleImpl(String configName, String configPath, Object defaultValue) {
        this.configName = configName;
        this.configPath = configPath;
        this.defaultValue = defaultValue;
    }
}