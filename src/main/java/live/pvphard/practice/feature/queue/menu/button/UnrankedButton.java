package live.pvphard.practice.feature.queue.menu.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.data.types.ProfileUnrankedKitData;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.leaderboard.LeaderboardService;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.feature.leaderboard.data.LeaderboardPlayerData;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 13/03/2025
 */
@AllArgsConstructor
public class UnrankedButton extends Button {
    protected final HardPractice plugin = HardPractice.getInstance();
    private final Queue queue;

    @Override
    public ItemStack getButtonItem(Player player) {
        Kit kit = this.queue.getKit();

        return new ItemBuilder(kit.getIcon())
                .name(kit.getMenuTitle())
                .durability(kit.getDurability())
                .hideMeta()
                .lore(this.getLore(kit, player))
                .hideMeta().build();
    }

    /**
     * Get the lore for the kit.
     *
     * @param kit the kit to get the lore for
     * @return the lore for the kit
     */
    private @NotNull List<String> getLore(Kit kit, Player player) {
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        ProfileUnrankedKitData kitData = profile != null
                ? profile.getProfileData().getUnrankedKitData().get(kit.getName())
                : null;
        List<String> topWinStreak = this.getTopEntries(kit, LeaderboardType.WIN_STREAK);
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);

        if (!kit.getDescription().isEmpty()) {
            Collections.addAll(lore,
                    "&7" + kit.getDescription(),
                    ""
            );
        }

        Collections.addAll(lore,
                "&b│ &rPlaying: &b" + this.queue.getQueueFightCount(),
                "&b│ &rIn queue: &b" + this.queue.getTotalPlayerCount(),
                "",
                "&f&lWinstreak: &b" + (kitData != null ? kitData.getWinstreak() : 0),
                "&f&lBest Winstreak: &b" + (kitData != null ? kitData.getBestWinstreak() : 0),
                topWinStreak.get(0),
                topWinStreak.get(1),
                topWinStreak.get(2),
                "",
                "&aClick to play.",
                CC.MENU_BAR
        );

        return lore;
    }

    private @NotNull List<String> getTopEntries(Kit kit, LeaderboardType type) {
        List<LeaderboardPlayerData> entries = HardPractice.getInstance()
                .getService(LeaderboardService.class)
                .getLeaderboardEntries(kit, type);
        List<String> topEntries = new ArrayList<>(3);

        for (int index = 0; index < 3; index++) {
            if (index < entries.size()) {
                LeaderboardPlayerData entry = entries.get(index);
                topEntries.add(" &f" + (index + 1) + ". &b" + entry.getName() + " &f- &b" + entry.getValue());
                continue;
            }

            topEntries.add(" &f" + (index + 1) + ". &bNobody &f- &bN/D");
        }

        return topEntries;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        if (clickType != ClickType.LEFT) return;

        ServerService serverService = HardPractice.getInstance().getService(ServerService.class);
        if (!serverService.isQueueingAllowed()) {
            player.sendMessage(this.plugin.getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.QUEUE_TEMPORARILY_DISABLED));
            player.closeInventory();
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        this.queue.addPlayer(player, this.queue.isRanked() ? profile.getProfileData().getRankedKitData().get(this.queue.getKit().getName()).getElo() : 0);
        this.playNeutral(player);

        PlayerUtil.reset(player, false, true);
        player.closeInventory();
    }
}
