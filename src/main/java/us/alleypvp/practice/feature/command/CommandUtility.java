package us.alleypvp.practice.feature.command;

import us.alleypvp.practice.core.profile.command.player.*;
import us.alleypvp.practice.core.profile.command.player.setting.toggle.*;
import us.alleypvp.practice.core.profile.command.player.setting.worldtime.*;
import us.alleypvp.practice.feature.arena.command.ArenaCommand;
import us.alleypvp.practice.feature.command.impl.other.InvSeeCommand;
import us.alleypvp.practice.feature.command.impl.other.MoreCommand;
import us.alleypvp.practice.feature.command.impl.other.RefillCommand;
import us.alleypvp.practice.feature.command.impl.other.SudoAllCommand;
import us.alleypvp.practice.feature.command.impl.other.troll.*;
import us.alleypvp.practice.feature.cooldown.command.CooldownResetCommand;
import us.alleypvp.practice.feature.hologram.command.HologramCommand;
import us.alleypvp.practice.feature.kit.command.KitCommand;
import us.alleypvp.practice.feature.kit.command.helper.impl.EnchantCommand;
import us.alleypvp.practice.feature.kit.command.helper.impl.PotionDurationCommand;
import us.alleypvp.practice.feature.kit.command.helper.impl.RemoveEnchantsCommand;
import us.alleypvp.practice.feature.kit.command.helper.impl.RenameCommand;
import us.alleypvp.practice.feature.match.command.player.*;
import us.alleypvp.practice.feature.queue.command.admin.QueueCommand;
import us.alleypvp.practice.feature.queue.command.player.LeaveQueueCommand;
import us.alleypvp.practice.feature.queue.command.player.QueuesCommand;
import us.alleypvp.practice.feature.queue.command.player.UnrankedCommand;
import us.alleypvp.practice.feature.spawn.command.SetSpawnCommand;
import us.alleypvp.practice.feature.spawn.command.SpawnCommand;
import us.alleypvp.practice.feature.spawn.command.SpawnItemsCommand;
import us.alleypvp.practice.feature.command.impl.main.KaosCommand;
import us.alleypvp.practice.feature.abilities.command.AbilityCommand;
import us.alleypvp.practice.feature.cosmetic.command.CosmeticCommand;
import us.alleypvp.practice.feature.division.command.DivisionCommand;
import us.alleypvp.practice.feature.emoji.command.EmojiCommand;
import us.alleypvp.practice.feature.event.command.EventCommand;
import us.alleypvp.practice.feature.event.command.EventJoinCommand;
import us.alleypvp.practice.feature.event.command.EventLeaveCommand;
import us.alleypvp.practice.feature.event.command.EventStartCommand;
import us.alleypvp.practice.feature.event.command.EventStopCommand;
import us.alleypvp.practice.feature.layout.command.LayoutCommand;
import us.alleypvp.practice.feature.leaderboard.command.LeaderboardCommand;
import us.alleypvp.practice.feature.level.command.LevelAdminCommand;
import us.alleypvp.practice.feature.server.command.ServiceCommand;
import us.alleypvp.practice.feature.tip.command.TipCommand;
import us.alleypvp.practice.feature.title.command.TitleCommand;
import us.alleypvp.practice.feature.duel.command.AcceptCommand;
import us.alleypvp.practice.feature.duel.command.DuelCommand;
import us.alleypvp.practice.feature.duel.command.DuelRequestsCommand;
import us.alleypvp.practice.feature.ffa.command.FFACommand;
import us.alleypvp.practice.feature.host.command.HostCommand;
import us.alleypvp.practice.feature.match.command.admin.MatchCommand;
import us.alleypvp.practice.feature.match.snapshot.command.InventoryCommand;
import us.alleypvp.practice.feature.party.command.PartyCommand;
import us.alleypvp.practice.core.profile.command.admin.PlaytimeCommand;
import us.alleypvp.practice.core.profile.data.command.ranked.RankedCommand;
import us.alleypvp.practice.core.profile.data.command.ranked.impl.RankedBanCommand;
import us.alleypvp.practice.core.profile.data.command.ranked.impl.RankedBanListCommand;
import us.alleypvp.practice.core.profile.data.command.ranked.impl.RankedUnbanCommand;
import us.alleypvp.practice.core.profile.data.command.ResetStatsCommand;
import us.alleypvp.practice.core.profile.data.command.coin.SetCoinsCommand;
import us.alleypvp.practice.core.profile.command.player.setting.MatchSettingsCommand;
import us.alleypvp.practice.core.profile.command.player.setting.PracticeSettingsCommand;
import us.alleypvp.practice.common.logger.Logger;
import us.alleypvp.practice.common.logger.command.ViewErrorCommand;
import lombok.experimental.UtilityClass;

/**
 * @author Emmy
 * @project Alley
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
            new UnrankedCommand();
            new MatchSettingsCommand();

            new ShopCommand();
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
