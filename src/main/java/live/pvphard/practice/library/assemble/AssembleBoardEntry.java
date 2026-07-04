package live.pvphard.practice.library.assemble;

import live.pvphard.practice.library.assemble.utility.AssembleUtility;
import lombok.Setter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Setter
public class AssembleBoardEntry {
    private final AssembleBoard board;
    private final String identifier;
    private String text;
    private Team team;

    /**
     * Assemble Board Entry
     *
     * @param board    that entry belongs to.
     * @param text     of entry.
     * @param position of entry.
     */
    public AssembleBoardEntry(AssembleBoard board, String text, int position) {
        this.board = board;
        this.identifier = this.board.getUniqueIdentifier(position);
        this.text = text;

        this.setup();
    }

    /**
     * Setup Board Entry.
     */
    public void setup() {
        final Scoreboard scoreboard = this.board.getScoreboard();
        if (scoreboard == null) return;

        String teamName = this.identifier;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        if (!team.getEntries().contains(this.identifier)) {
            team.addEntry(this.identifier);
        }

        if (!this.board.getEntries().contains(this)) {
            this.board.getEntries().add(this);
        }

        this.team = team;
    }

    /**
     * Send the board entry update to the player.
     *
     * @param position of entry.
     */
    public void send(int position) {
        String[] split = AssembleUtility.splitTeamText(this.text);
        this.team.setPrefix(split[0]);
        this.team.setSuffix(split[1]);

        this.board.getObjective().getScore(this.identifier).setScore(position);
    }

    public void remove() {
        this.board.getIdentifiers().remove(this.identifier);
        this.board.getScoreboard().resetScores(this.identifier);
    }
}