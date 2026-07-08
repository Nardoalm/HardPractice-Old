package live.pvphard.practice.bootstrap.listener.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.cosmetic.CosmeticListener;
import live.pvphard.practice.feature.cosmetic.listener.TrailListener;
import live.pvphard.practice.feature.match.listener.types.*;
import live.pvphard.practice.library.menu.MenuListener;
import live.pvphard.practice.feature.arena.listener.ArenaListener;
import live.pvphard.practice.feature.combat.listener.CombatListener;
import live.pvphard.practice.feature.event.listener.EventListener;
import live.pvphard.practice.feature.hotbar.listener.HotbarListener;
import live.pvphard.practice.feature.queue.listener.QueueListener;
import live.pvphard.practice.feature.server.listener.CraftingListener;
import live.pvphard.practice.feature.spawn.listener.SpawnListener;
import live.pvphard.practice.bootstrap.listener.ListenerService;
import live.pvphard.practice.feature.emoji.listener.EmojiListener;
import live.pvphard.practice.feature.item.listener.ItemListener;
import live.pvphard.practice.feature.layout.listener.LayoutListener;
import live.pvphard.practice.feature.ffa.listener.FFAListener;
import live.pvphard.practice.feature.ffa.listener.FFABlockListener;
import live.pvphard.practice.feature.ffa.listener.FFACuboidListener;
import live.pvphard.practice.feature.ffa.listener.FFADamageListener;
import live.pvphard.practice.feature.ffa.listener.FFADisconnectListener;
import live.pvphard.practice.feature.match.listener.MatchListener;
import live.pvphard.practice.feature.match.snapshot.listener.SnapshotListener;
import live.pvphard.practice.feature.party.listener.PartyListener;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.profile.listener.ProfileListener;

import java.util.Arrays;

/**
 * @author Emmy
 * @project hard-practice
 * @since 16/07/2025
 */
@Service(provides = ListenerService.class, priority = 1000)
public class ListenerServiceImpl implements ListenerService {

    @Override
    public void initialize(HardContext context) {
        this.registerListeners(context.getPlugin());
    }

    @Override
    public void registerListeners(HardPractice plugin) {
        Arrays.asList(
                new ProfileListener(),
                new HotbarListener(),
                new EventListener(),
                new PartyListener(),
                new ArenaListener(),
                new MenuListener(),
                new SpawnListener(),

                new EmojiListener(),
                new CombatListener(),
                new QueueListener(),
                new LayoutListener(),
                new SnapshotListener(),
                new CraftingListener(),

                new ItemListener(),

                new FFAListener(), new FFACuboidListener(),
                new FFABlockListener(), new FFADamageListener(), new FFADisconnectListener(),

                new MatchListener(), new MatchInteractListener(),
                new MatchPearlListener(), new MatchDisconnectListener(),
                new MatchDamageListener(), new MatchChatListener(), new MatchBlockListener(),

                new CosmeticListener(),
                new TrailListener()

        ).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }
}
