package live.pvphard.practice.feature.leaderboard.menu.button;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.leaderboard.data.LeaderboardPlayerData;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardKitButton extends Button {
    private final Kit kit;
    private final List<LeaderboardPlayerData> leaderboard;
    private final LeaderboardType type;

    public LeaderboardKitButton(Kit kit, List<LeaderboardPlayerData> leaderboard, LeaderboardType type) {
        this.kit = kit;
        this.leaderboard = leaderboard;
        this.type = type;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder builder = new ItemBuilder(this.kit.getIcon())
                .name(this.kit.getMenuTitle())
                .durability(this.kit.getDurability())
                .hideMeta();

        switch (this.type) {
            case RANKED:
                return builder.lore(generateLore("Elo")).build();
            case UNRANKED:
                return builder.lore(generateLore("Wins")).build();
            case FFA:
                return builder.lore(generateLore("Kills")).build();
            case WIN_STREAK:
                return builder.lore(generateLore("Streak")).build();
            case UNRANKED_MONTHLY:
            case TOURNAMENT:
            default:
                return this.inDevelopment();
        }
    }

    private List<String> generateLore(String statName) {
        if (this.leaderboard.isEmpty()) {
            return Arrays.asList("", "&7No records found for this kit yet.");
        }

        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);

        List<String> topEntries = this.leaderboard.stream()
                .limit(10)
                .map(data -> {
                    int currentRank = this.leaderboard.indexOf(data) + 1;

                    String rankPrefix;
                    switch (currentRank) {
                        case 1:
                            rankPrefix = "&b&l✫" + currentRank;
                            break;
                        case 2:
                            rankPrefix = "&7&l✫" + currentRank;
                            break;
                        case 3:
                            rankPrefix = "&c&l✫" + currentRank;
                            break;
                        default:
                            rankPrefix = "&b" + currentRank + ".";
                            break;
                    }

                    return rankPrefix + " &f" + data.getName() + " &7- &f" + data.getValue() + " " + statName;
                })
                .collect(Collectors.toList());

        lore.addAll(topEntries);
        lore.add(CC.MENU_BAR);
        return lore;
    }

    private ItemStack inDevelopment() {
        return new ItemBuilder(Material.BARRIER)
                .name("&c&lComing Soon")
                .lore(
                        CC.MENU_BAR,
                        "&7This leaderboard is still",
                        "&7in development. Check back",
                        "&7later.",
                        CC.MENU_BAR
                )
                .build();
    }
}