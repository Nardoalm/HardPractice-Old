package live.pvphard.practice.core.profile.menu.match.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.constants.MessageConstant;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.time.DateFormat;
import live.pvphard.practice.common.time.DateFormatter;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.match.data.MatchData;
import live.pvphard.practice.feature.match.data.types.MatchDataSolo;
import live.pvphard.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class MatchHistoryViewButton extends Button {
    protected final MatchData matchData;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.matchData instanceof MatchDataSolo) {
            MatchDataSolo matchDataSolo = (MatchDataSolo) this.matchData;

            UUID winnerUUID = matchDataSolo.getWinner();
            UUID loserUUID = matchDataSolo.getLoser();

            String winnerName = Bukkit.getOfflinePlayer(winnerUUID).getName();
            String loserName = Bukkit.getOfflinePlayer(loserUUID).getName();

            DateFormatter dateFormatter = new DateFormatter(DateFormat.DATE_PLUS_TIME, matchDataSolo.getCreationTime());
            String date = dateFormatter.getDateFormat().format(dateFormatter.getDate());

            String rankedStatus = matchDataSolo.isRanked() ? "&bRanked" : "&9Unranked";

            Kit kit = HardPractice.getInstance().getService(KitService.class).getKit(matchDataSolo.getKit());
            Arena arena = HardPractice.getInstance().getService(ArenaService.class).getArenaByName(matchDataSolo.getArena());

            return new ItemBuilder(Material.SKULL_ITEM)
                    .name("&a&l" + winnerName + " &7vs &c&l" + loserName + " &7(" + rankedStatus + "&7)")
                    .setSkull(winnerName)
                    .lore(
                            "&7" + date,
                            "",
                            "&b&lParticipants",
                            " &f● Winner: &a" + winnerName,
                            " &f● Loser: &c" + loserName,
                            "",
                            "&b&lMatch Details",
                            " &f● Kit: &b" + kit.getDisplayName(),
                            " &f● Arena: &b" + arena.getDisplayName(),
                            "",
                            "&aClick to view more details!"
                    )
                    .hideMeta()
                    .durability(3)
                    .build();
        } else {
            return new ItemBuilder(Material.BARRIER)
                    .name("&c&lNot Implemented")
                    .lore(
                            "&fThis features has not been implemented for team matches yet."
                    )
                    .hideMeta()
                    .build();
        }
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        player.sendMessage(MessageConstant.IN_DEVELOPMENT);
        player.closeInventory();
    }
}