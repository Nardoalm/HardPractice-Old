package us.alleypvp.practice.api.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.api.AlleyPracticeAPI;
import us.alleypvp.practice.api.model.PracticeNametagContext;
import us.alleypvp.practice.bootstrap.lifecycle.Service;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.SettingsLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.feature.combat.CombatService;
import us.alleypvp.practice.feature.match.Match;
import us.alleypvp.practice.feature.match.internal.types.DefaultMatch;
import us.alleypvp.practice.feature.match.model.GameParticipant;
import us.alleypvp.practice.feature.match.model.internal.MatchGamePlayer;
import us.alleypvp.practice.feature.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class AlleyPracticeAPIImpl implements AlleyPracticeAPI {
    private static final String[] SAFE_TEAM_COLORS = {
            "&c", "&9", "&a", "&e", "&5", "&b", "&b", "&d", "&4", "&3", "&2", "&1", "&f", "&0"
    };

    private final AlleyPractice plugin;

    public AlleyPracticeAPIImpl(AlleyPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInMatch(Player player) {
        return this.getMatchParticipant(player) != null;
    }

    @Override
    public boolean isSpectating(Player player) {
        Profile profile = this.getProfile(player);
        return profile != null && profile.getState() == ProfileState.SPECTATING && profile.getMatch() != null;
    }

    @Override
    public boolean isQueueing(Player player) {
        Profile profile = this.getProfile(player);
        return profile != null && profile.getState() == ProfileState.WAITING && profile.getQueueProfile() != null;
    }

    @Override
    public boolean isInCombat(Player player) {
        if (player == null) {
            return false;
        }

        CombatService combatService = this.getService(CombatService.class);
        return combatService != null && combatService.isPlayerInCombat(player.getUniqueId());
    }

    @Override
    public boolean isInLobby(Player player) {
        Profile profile = this.getProfile(player);
        return profile != null && profile.getState() == ProfileState.LOBBY;
    }

    @Override
    public String getMatchId(Player player) {
        Match match = this.getCurrentMatch(player);
        return match != null ? this.safe(match.getMatchId()) : "";
    }

    @Override
    public String getMatchMode(Player player) {
        Match match = this.getCurrentMatch(player);
        if (match == null) {
            return "";
        }

        String rankSegment = match.isRanked() ? "RANKED" : "UNRANKED";
        String teamSegment = match.isTeamMatch() ? "TEAM" : "SOLO";
        return rankSegment + "_" + teamSegment;
    }

    @Override
    public String getArenaName(Player player) {
        Match match = this.getCurrentMatch(player);
        if (match == null || match.getArena() == null) {
            return "";
        }

        String arenaName = this.safe(match.getArena().getName());
        if (!arenaName.isEmpty()) {
            return arenaName;
        }

        return this.safe(match.getArena().getDisplayName());
    }

    @Override
    public String getTeamId(Player player) {
        Match match = this.getMatchParticipant(player);
        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player, match);
        int participantIndex = this.getParticipantIndex(match, participant);
        if (match == null || participantIndex < 0) {
            return "";
        }

        return match.getMatchId() + ":team-" + (participantIndex + 1);
    }

    @Override
    public String getTeamDisplayName(Player player) {
        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player, this.getMatchParticipant(player));
        return participant != null ? this.safe(participant.getConjoinedNames()) : "";
    }

    @Override
    public String getTeamColor(Player player) {
        Match match = this.getMatchParticipant(player);
        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player, match);
        int participantIndex = this.getParticipantIndex(match, participant);
        if (match == null || participant == null || participantIndex < 0) {
            return "";
        }

        ChatColor color = null;
        if (match instanceof DefaultMatch) {
            color = ((DefaultMatch) match).getTeamColor(participant);
        }

        return this.normalizeTeamColor(color, participantIndex);
    }

    @Override
    public boolean shouldOverrideDefaultNametag(Player player) {
        return this.getMatchParticipant(player) != null;
    }

    @Override
    public PracticeNametagContext getNametagContext(Player player) {
        if (!this.shouldOverrideDefaultNametag(player)) {
            return PracticeNametagContext.empty();
        }

        String teamColor = this.getTeamColor(player);
        return new PracticeNametagContext(true, "", "", teamColor, this.getMatchPriority());
    }

    @Override
    public boolean isInEvent(Player player) {
        Profile profile = this.getProfile(player);
        if (profile == null) {
            return false;
        }

        return profile.getState() == ProfileState.PLAYING_EVENT || profile.getState() == ProfileState.PLAYING_TOURNAMENT;
    }

    @Override
    public boolean isInParty(Player player) {
        Party party = this.getParty(player);
        return player != null && party != null && party.getMembers() != null && party.getMembers().contains(player.getUniqueId());
    }

    @Override
    public String getPartyId(Player player) {
        Party party = this.getParty(player);
        if (party == null || party.getLeader() == null) {
            return "";
        }

        UUID leaderId = party.getLeader().getUniqueId();
        return leaderId != null ? leaderId.toString() : "";
    }

    @Override
    public String getPartyRole(Player player) {
        Party party = this.getParty(player);
        if (player == null || party == null || party.getMembers() == null || !party.getMembers().contains(player.getUniqueId())) {
            return "";
        }

        return party.isLeader(player) ? "LEADER" : "MEMBER";
    }

    private Profile getProfile(Player player) {
        if (player == null) {
            return null;
        }

        ProfileService profileService = this.getService(ProfileService.class);
        return profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
    }

    private Match getCurrentMatch(Player player) {
        Profile profile = this.getProfile(player);
        return profile != null ? profile.getMatch() : null;
    }

    private Match getMatchParticipant(Player player) {
        Profile profile = this.getProfile(player);
        Match match = profile != null ? profile.getMatch() : null;
        if (profile == null || profile.getState() != ProfileState.PLAYING || match == null) {
            return null;
        }

        return this.getParticipant(player, match) != null ? match : null;
    }

    private GameParticipant<MatchGamePlayer> getParticipant(Player player, Match match) {
        if (player == null || match == null) {
            return null;
        }

        return match.getParticipant(player);
    }

    private int getParticipantIndex(Match match, GameParticipant<MatchGamePlayer> participant) {
        if (match == null || participant == null) {
            return -1;
        }

        List<GameParticipant<MatchGamePlayer>> participants = match.getParticipants();
        if (participants == null || participants.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i) == participant) {
                return i;
            }
        }

        return -1;
    }

    private Party getParty(Player player) {
        Profile profile = this.getProfile(player);
        return profile != null ? profile.getParty() : null;
    }

    private int getMatchPriority() {
        LocaleService localeService = this.getService(LocaleService.class);
        return localeService != null ? Math.max(0, localeService.getInt(SettingsLocaleImpl.PRACTICE_API_NAMETAG_PRIORITY)) : 100;
    }

    private String normalizeTeamColor(ChatColor color, int participantIndex) {
        if (color != null && color.isColor()) {
            char colorChar = Character.toLowerCase(color.getChar());
            if (colorChar != '7' && colorChar != '8') {
                return "&" + colorChar;
            }
        }

        if (participantIndex < 0) {
            return "";
        }

        return SAFE_TEAM_COLORS[participantIndex % SAFE_TEAM_COLORS.length];
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private <T extends Service> T getService(Class<T> serviceClass) {
        try {
            return this.plugin.getService(serviceClass);
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
