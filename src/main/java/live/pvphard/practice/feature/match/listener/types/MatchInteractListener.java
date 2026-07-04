package live.pvphard.practice.feature.match.listener.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingCheckpoint;
import live.pvphard.practice.feature.match.internal.types.CheckpointMatch;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Emmy
 * @project Hard
 * @since 08/02/2025
 */
public class MatchInteractListener implements Listener {
    @EventHandler
    private void handleParkourInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() != ProfileState.PLAYING) return;
        if (!profile.getMatch().getKit().isSettingEnabled(KitSettingCheckpoint.class)) return;

        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Block block = event.getClickedBlock();

        if (ListenerUtil.notSteppingOnPlate(block)) return;

        CheckpointMatch matchCheckpoint = (CheckpointMatch) profile.getMatch();
        MatchGamePlayer matchGamePlayer = matchCheckpoint.getGamePlayer(player);
        if (matchGamePlayer == null) return;
        if (ListenerUtil.checkSteppingOnIronPressurePlate(block)) {
            Location checkpointLocation = player.getLocation();

            matchGamePlayer.setCheckpoint(checkpointLocation);

            boolean isNewCheckpoint = matchGamePlayer.getCheckpoints().stream()
                    .noneMatch(location -> location.getX() == checkpointLocation.getX() &&
                            location.getY() == checkpointLocation.getY() &&
                            location.getZ() == checkpointLocation.getZ());

            if (isNewCheckpoint) {
                matchGamePlayer.getCheckpoints().add(checkpointLocation);
                matchGamePlayer.setCheckpointCount(matchGamePlayer.getCheckpointCount() + 1);

                LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

                if (localeService.getBoolean(VisualsLocaleImpl.TITLE_CHECKPOINT_ENABLED_BOOLEAN)) {
                    String header = localeService.getString(VisualsLocaleImpl.TITLE_CHECKPOINT_HEADER);
                    String footer = localeService.getString(VisualsLocaleImpl.TITLE_CHECKPOINT_FOOTER)
                            .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                            .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                            .replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
                    int fadeIn = localeService.getInt(VisualsLocaleImpl.TITLE_CHECKPOINT_FADE_IN);
                    int stay = localeService.getInt(VisualsLocaleImpl.TITLE_CHECKPOINT_STAY);
                    int fadeOut = localeService.getInt(VisualsLocaleImpl.TITLE_CHECKPOINT_FADEOUT);

                    HardPractice.getInstance().getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                            player,
                            header,
                            footer,
                            fadeIn, stay, fadeOut
                    );
                }
            }

            return;
        }

        if (ListenerUtil.checkSteppingOnGoldPressurePlate(block)) {
            GameParticipant<MatchGamePlayer> opponent = matchCheckpoint.getParticipantA().containsPlayer(player.getUniqueId())
                    ? matchCheckpoint.getParticipantB()
                    : matchCheckpoint.getParticipantA();

            opponent.setLostCheckpoint(true);
            opponent.getPlayers().forEach(gamePlayer -> gamePlayer.setDead(true));
            opponent.getPlayers().stream().findAny().ifPresent(gamePlayer -> {
                matchCheckpoint.handleDeath(gamePlayer.getTeamPlayer(), EntityDamageEvent.DamageCause.CUSTOM);
            });
        }
    }
}