package live.pvphard.practice.feature.match.task.other;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MatchRespawnTask extends BukkitRunnable {
    protected final Player player;
    protected final Match match;
    private int count;

    public MatchRespawnTask(Player player, Match match, int count) {
        this.player = player;
        this.match = match;
        this.count = count;
    }

    @Override
    public void run() {
        if (this.player == null || !this.player.isOnline()) {
            this.cancel();
            return;
        }

        if (this.match.getState() == MatchState.ENDING_MATCH || this.match.getState() == MatchState.ENDING_ROUND) {
            this.cancel();
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(this.player.getUniqueId());
        if (profile == null || profile.getMatch() != this.match || profile.getState() != ProfileState.PLAYING) {
            this.cancel();
            return;
        }

        if (this.count == 0) {
            this.cancel();
            this.match.handleRespawn(this.player);
            return;
        }

        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);
        if (localeService.getBoolean(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_ENABLED_BOOLEAN)) {
            String header = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_HEADER).replace("{seconds}", String.valueOf(this.count));
            String footer = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_FOOTER).replace("{seconds}", String.valueOf(this.count));
            int fadeIn = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_FADE_IN);
            int stay = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_STAY);
            int fadeOut = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_RESPAWNING_FADEOUT);

            HardPractice.getInstance().getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                    player,
                    header,
                    footer,
                    fadeIn, stay, fadeOut
            );
        }

        List<String> messageFormat = localeService.getStringList(GameMessagesLocaleImpl.MATCH_RESPAWNING_MESSAGE_FORMAT);
        if (localeService.getBoolean(GameMessagesLocaleImpl.MATCH_RESPAWNING_MESSAGE_ENABLED_BOOLEAN)) {
            messageFormat.forEach(message -> this.player.sendMessage(CC.translate(message.replace("{seconds}", String.valueOf(this.count)))));
        }

        this.count--;
    }
}