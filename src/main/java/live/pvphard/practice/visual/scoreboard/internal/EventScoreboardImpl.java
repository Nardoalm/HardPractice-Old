package live.pvphard.practice.visual.scoreboard.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.feature.event.EventPhase;
import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.feature.event.model.ActiveEvent;
import live.pvphard.practice.visual.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventScoreboardImpl implements Scoreboard {
    @Override
    public List<String> getLines(Profile profile) {
        if (profile == null) {
            return Collections.emptyList();
        }

        Player player = Bukkit.getPlayer(profile.getUuid());
        if (player == null || !player.isOnline()) {
            return Collections.emptyList();
        }

        EventService eventService = HardPractice.getInstance().getService(EventService.class);
        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        if (eventService == null || configService == null || configService.getScoreboardConfig() == null) {
            return Collections.emptyList();
        }

        ActiveEvent activeEvent = eventService.getActiveEvent();
        if (activeEvent == null) {
            return Collections.singletonList("&cNenhum evento ativo.");
        }

        String path = activeEvent.getPhase() == EventPhase.RUNNING
                ? "scoreboard.lines.event.playing"
                : "scoreboard.lines.event.waiting";

        List<String> template = configService.getScoreboardConfig().getStringList(path);
        List<String> lines = new ArrayList<>();

        Player fighterA = activeEvent.getCurrentFighterAPlayer();
        Player fighterB = activeEvent.getCurrentFighterBPlayer();

        for (String line : template) {
            lines.add(CC.translate(line)
                    .replace("{event-name}", activeEvent.getDefinition().getDisplayName())
                    .replace("{event-host}", activeEvent.getHostName())
                    .replace("{event-players}", String.valueOf(activeEvent.getParticipants().size()))
                    .replace("{event-max}", String.valueOf(activeEvent.getDefinition().getMaximumPlayers()))
                    .replace("{event-countdown}", activeEvent.getPhase() == EventPhase.RUNNING ? "Iniciado" : activeEvent.getCountdownRemaining() + "s")
                    .replace("{event-round}", String.valueOf(activeEvent.getCurrentRound()))
                    .replace("{fighter-a}", fighterA != null ? fighterA.getName() : "---")
                    .replace("{fighter-b}", fighterB != null ? fighterB.getName() : "---")
                    .replace("{fighter-a-ping}", fighterA != null ? String.valueOf(this.getPing(fighterA)) : "0")
                    .replace("{fighter-b-ping}", fighterB != null ? String.valueOf(this.getPing(fighterB)) : "0"));
        }

        return lines;
    }

    @Override
    public List<String> getLines(Profile profile, Player player) {
        return this.getLines(profile);
    }
}
