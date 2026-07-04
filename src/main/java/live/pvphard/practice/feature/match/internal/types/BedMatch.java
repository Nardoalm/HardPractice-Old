package live.pvphard.practice.feature.match.internal.types;

import org.bukkit.Bukkit;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.queue.Queue;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class BedMatch extends DefaultMatch {
    private final GameParticipant<MatchGamePlayer> participantA;
    private final GameParticipant<MatchGamePlayer> participantB;

    public BedMatch(Queue queue, Kit kit, Arena arena, boolean ranked, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB) {
        super(queue, kit, arena, ranked, participantA, participantB);
        this.participantA = participantA;
        this.participantB = participantB;
    }

    @Override
    public boolean canEndRound() {
        return (this.participantA.isAllEliminated() || this.participantB.isAllEliminated())
                || (this.participantA.getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected)
                || this.participantB.getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected));
    }

    @Override
    public boolean canStartRound() {
        return !this.participantA.isAllEliminated() && !this.participantB.isAllEliminated();
    }

    @Override
    public void handleParticipant(Player player, MatchGamePlayer gamePlayer) {
        GameParticipant<MatchGamePlayer> gameParticipant = this.getParticipantA().containsPlayer(player.getUniqueId())
                ? this.getParticipantA()
                : this.getParticipantB();

        if (gameParticipant.isBedBroken()) {
            GameParticipant<MatchGamePlayer> participant = getParticipant(player);
            if (participant == null) {
                return;
            }

            gamePlayer.setEliminated(true);
        }

        super.handleParticipant(player, gamePlayer);
    }

    @Override
    protected boolean shouldHandleRegularRespawn(Player player) {
        return false;
    }

    @Override
    public void handleRespawn(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);

        PlayerUtil.reset(player, true, true);

        Location spawnLocation = this.getParticipants().get(0).containsPlayer(player.getUniqueId()) ? this.getArena().getPos1() : this.getArena().getPos2();
        ListenerUtil.teleportAndClearSpawn(player, spawnLocation);

        this.giveLoadout(player, this.getKit());
        this.applyColorKit(player);

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                onlinePlayer.showPlayer(player);
            }
        });
    }

    public void alertBedDestruction(Player breaker, GameParticipant<MatchGamePlayer> opponentParticipant) {
        LocaleService localeService = this.plugin.getService(LocaleService.class);
        TitleReflectionServiceImpl titleService = this.plugin.getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class);

        if (localeService.getBoolean(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_ENABLED_BOOLEAN)) {
            String bedDestroyedHeader = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_HEADER);
            String bedDestroyedFooter = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_FOOTER);
            int fadeIn = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_FADE_IN);
            int stay = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_STAY);
            int fadeOut = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_BED_DESTROYED_FADEOUT);

            opponentParticipant.getPlayers().forEach(matchGamePlayer -> {
                Player player = this.plugin.getServer().getPlayer(matchGamePlayer.getUuid());
                titleService.sendTitle(player, bedDestroyedHeader, bedDestroyedFooter, fadeIn, stay, fadeOut);
            });
        }

        this.playSound(opponentParticipant, Sound.WITHER_DEATH);

        GameParticipant<MatchGamePlayer> breakerParticipant = this.getParticipant(breaker);
        this.playSound(breakerParticipant, Sound.ENDERDRAGON_GROWL);

        if (localeService.getBoolean(GameMessagesLocaleImpl.MATCH_BED_DESTRUCTION_MESSAGE_ENABLED_BOOLEAN)) {
            List<String> message = localeService.getStringList(GameMessagesLocaleImpl.MATCH_BED_DESTRUCTION_MESSAGE_FORMAT);

            String formattedBedName = getFormattedBedName(opponentParticipant);

            message.forEach(line -> {
                String formattedLine = line
                        .replace("{bed-color}", String.valueOf(this.getTeamColor(opponentParticipant)))
                        .replace("{breaker-color}", String.valueOf(this.getTeamColor(breakerParticipant)))
                        .replace("{bed}", formattedBedName)
                        .replace("{breaker}", PlayerDisplayUtil.resolveCurrentNick(breaker, breaker.getName()));
                this.sendMessage(formattedLine);
            });
        }
    }

    public String getFormattedBedName(GameParticipant<MatchGamePlayer> participant) {
        org.bukkit.ChatColor color = this.getTeamColor(participant);
        switch (color) {
            case RED: return "&cRed Bed";
            case BLUE: return "&9Blue Bed";
            case GREEN: return "&aGreen Bed";
            case YELLOW: return "&eYellow Bed";
            case AQUA: return "&3Aqua Bed";
            case LIGHT_PURPLE: return "&dPink Bed";
            case GOLD: return "&6Orange Bed";
            case WHITE: return "&fWhite Bed";
            case GRAY: return "&7Gray Bed";
            case DARK_PURPLE: return "&5Purple Bed";
            default: return color + color.name() + " Bed";
        }
    }

    public boolean isNearBed(Block block) {
        Location loc = block.getLocation();
        org.bukkit.World world = loc.getWorld();
        int centerX = loc.getBlockX();
        int centerY = loc.getBlockY();
        int centerZ = loc.getBlockZ();

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    if (world.getBlockAt(centerX + x, centerY + y, centerZ + z).getType() == Material.BED_BLOCK) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}