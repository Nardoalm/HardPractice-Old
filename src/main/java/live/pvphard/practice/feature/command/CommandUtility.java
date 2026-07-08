package live.pvphard.practice.feature.command;

import live.pvphard.practice.core.profile.command.player.ChallengesCommand;
import live.pvphard.practice.core.profile.command.player.LevelCommand;
import live.pvphard.practice.core.profile.command.player.MatchHistoryCommand;
import live.pvphard.practice.core.profile.command.player.StatsCommand;
import live.pvphard.practice.core.profile.command.player.setting.toggle.*;
import live.pvphard.practice.core.profile.command.player.setting.worldtime.*;
import live.pvphard.practice.feature.command.impl.other.troll.*;
import live.pvphard.practice.feature.match.command.player.*;
import live.pvphard.practice.feature.arena.command.ArenaCommand;
import live.pvphard.practice.feature.command.impl.other.InvSeeCommand;
import live.pvphard.practice.feature.command.impl.other.MoreCommand;
import live.pvphard.practice.feature.command.impl.other.RefillCommand;
import live.pvphard.practice.feature.command.impl.other.SudoAllCommand;
import live.pvphard.practice.feature.cooldown.command.CooldownResetCommand;
import live.pvphard.practice.feature.hologram.command.HologramCommand;
import live.pvphard.practice.feature.kit.command.KitCommand;
import live.pvphard.practice.feature.kit.command.helper.impl.EnchantCommand;
import live.pvphard.practice.feature.kit.command.helper.impl.PotionDurationCommand;
import live.pvphard.practice.feature.kit.command.helper.impl.RemoveEnchantsCommand;
import live.pvphard.practice.feature.kit.command.helper.impl.RenameCommand;
import live.pvphard.practice.feature.queue.command.admin.QueueCommand;
import live.pvphard.practice.feature.queue.command.player.LeaveQueueCommand;
import live.pvphard.practice.feature.queue.command.player.QueuesCommand;
import live.pvphard.practice.feature.queue.command.player.RequeueCommand;
import live.pvphard.practice.feature.queue.command.player.UnrankedCommand;
import live.pvphard.practice.feature.spawn.command.SetSpawnCommand;
import live.pvphard.practice.feature.spawn.command.SpawnCommand;
import live.pvphard.practice.feature.spawn.command.SpawnItemsCommand;
import live.pvphard.practice.feature.command.impl.main.KaosCommand;
import live.pvphard.practice.feature.abilities.command.AbilityCommand;
import live.pvphard.practice.feature.cosmetic.command.CosmeticCommand;
import live.pvphard.practice.feature.division.command.DivisionCommand;
import live.pvphard.practice.feature.emoji.command.EmojiCommand;
import live.pvphard.practice.feature.event.command.EventCommand;
import live.pvphard.practice.feature.event.command.EventJoinCommand;
import live.pvphard.practice.feature.event.command.EventLeaveCommand;
import live.pvphard.practice.feature.event.command.EventStartCommand;
import live.pvphard.practice.feature.event.command.EventStopCommand;
import live.pvphard.practice.feature.layout.command.LayoutCommand;
import live.pvphard.practice.feature.leaderboard.command.LeaderboardCommand;
import live.pvphard.practice.feature.level.command.LevelAdminCommand;
import live.pvphard.practice.feature.server.command.ServiceCommand;
import live.pvphard.practice.feature.tip.command.TipCommand;
import live.pvphard.practice.feature.title.command.TitleCommand;
import live.pvphard.practice.feature.duel.command.AcceptCommand;
import live.pvphard.practice.feature.duel.command.DuelCommand;
import live.pvphard.practice.feature.duel.command.DuelRequestsCommand;
import live.pvphard.practice.feature.ffa.command.FFACommand;
import live.pvphard.practice.feature.host.command.HostCommand;
import live.pvphard.practice.feature.match.command.admin.MatchCommand;
import live.pvphard.practice.feature.match.snapshot.command.InventoryCommand;
import live.pvphard.practice.feature.party.command.PartyCommand;
import live.pvphard.practice.core.profile.command.admin.PlaytimeCommand;
import live.pvphard.practice.core.profile.data.command.ranked.RankedCommand;
import live.pvphard.practice.core.profile.data.command.ranked.impl.RankedBanCommand;
import live.pvphard.practice.core.profile.data.command.ranked.impl.RankedBanListCommand;
import live.pvphard.practice.core.profile.data.command.ranked.impl.RankedUnbanCommand;
import live.pvphard.practice.core.profile.data.command.ResetStatsCommand;
import live.pvphard.practice.core.profile.data.command.coin.SetCoinsCommand;
import live.pvphard.practice.core.profile.command.player.setting.MatchSettingsCommand;
import live.pvphard.practice.core.profile.command.player.setting.PracticeSettingsCommand;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.logger.command.ViewErrorCommand;
import lombok.experimental.UtilityClass;

/**
 * @author Emmy
 * @project Hard
 * @date 31/12/2024 - 23:24
 */
@UtilityClass
public class CommandUtility {
    /**
     * Registers all commands.
     */
    public void registerCommands() {
        Logger.logTimeWithAction("registered", "Commands", () -> {
            new KaosCommand();

            new KitCommand();
            new ArenaCommand();
            new MatchCommand();
            new RankedCommand();
            new RankedBanCommand();
            new RankedUnbanCommand();
            new RankedUnbanCommand();
            new RankedBanListCommand();
            new QueueCommand();
            new FFACommand();
            new CosmeticCommand();
            new DivisionCommand();
            new TitleCommand();
            new LevelAdminCommand();
            new LevelCommand();
            new ServiceCommand();
            new EnchantCommand();
            new SudoAllCommand();
            new InvSeeCommand();
            new MoreCommand();
            new PotionDurationCommand();
            new RefillCommand();
            new RemoveEnchantsCommand();
            new RenameCommand();
            new PlaytimeCommand();
            new SpawnItemsCommand();
            new SetSpawnCommand();
            new SpawnCommand();
            new SetCoinsCommand();

            new FakeExplosionCommand();
            new HeartAttackCommand();
            new LaunchCommand();
            new PushCommand();
            new StrikeCommand();
            new TrollCommand();

            new CooldownResetCommand();

            new ViewErrorCommand();

            new AbilityCommand();

            new DayCommand();
            new NightCommand();
            new SunsetCommand();
            new ResetTimeCommand();
            new TogglePartyInvitesCommand();
            new TogglePartyMessagesCommand();
            new ToggleScoreboardCommand();
            new ToggleScoreboardLinesCommand();
            new TogglePingRangeCommand();
            new ToggleWorldTimeCommand();

            new PartyCommand();
            new AcceptCommand();
            new DuelCommand();
            new DuelRequestsCommand();
            new InventoryCommand();
            new PracticeSettingsCommand();
            new LeaderboardCommand();
            new HologramCommand();
            new ResetStatsCommand();
            new StatsCommand();
            new SpectateCommand();
            new ViewPlayersCommand();
            new LeaveSpectatorCommand();
            new LeaveMatchCommand();
            new CurrentMatchesCommand();
            new LeaveQueueCommand();
            new QueuesCommand();
            new RequeueCommand();
            new UnrankedCommand();
            new MatchSettingsCommand();

            new ChallengesCommand();
            new MatchHistoryCommand();
            new TipCommand();
            new LayoutCommand();

            new HostCommand();
            new EventCommand();
            new EventJoinCommand();
            new EventLeaveCommand();
            new EventStartCommand();
            new EventStopCommand();
            new EmojiCommand();
        });
    }
}
