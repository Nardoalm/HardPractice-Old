package live.pvphard.practice.feature.queue.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.QueueType;
import live.pvphard.practice.feature.queue.menu.button.BotButton;
import live.pvphard.practice.feature.queue.menu.button.UnrankedButton;
import live.pvphard.practice.feature.queue.menu.extra.button.QueueModeSwitcherButton;
import live.pvphard.practice.feature.ffa.FFAMatch;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.feature.ffa.menu.FFAButton;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.LoreHelper;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 23/05/2024 - 01:28
 */
public class QueuesMenuModern extends Menu {
    /**
     * Get the title of the menu.
     *
     * @param player the player to get the title for
     * @return the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        return "&b&l" + profile.getQueueType().getMenuTitle();
    }

    /**
     * Get the buttons for the menu.
     *
     * @param player the player to get the buttons for
     * @return the buttons for the menu
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        QueueService queueService = HardPractice.getInstance().getService(QueueService.class);
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        buttons.put(2, new QueuesButtonModern("&b&lUnranked", Material.DIAMOND_SWORD, 0, Arrays.asList(
                CC.MENU_BAR,
                "&71v1 casuais sem",
                "&7penalidade por derrota.",
                "",
                "&b│ &fJogadores: &b" + queueService.getPlayerCountOfGameType("Unranked"),
                "",
                this.getLore(profile, QueueType.UNRANKED),
                CC.MENU_BAR
        )));

        buttons.put(4, new QueuesButtonModern("&b&lUnranked Duos", Material.GOLD_SWORD, 0, Arrays.asList(
                CC.MENU_BAR,
                "&72v2 casuais sem",
                "&7penalidade por derrota.",
                "",
                "&b│ &fJogadores: &b" + queueService.getPlayerCountOfGameType("Duos"),
                "",
                this.getLore(profile, QueueType.DUOS),
                CC.MENU_BAR
        )));

        buttons.put(6, new QueuesButtonModern("&b&lFFA", Material.GOLD_AXE, 0, Arrays.asList(
                CC.MENU_BAR,
                "&7Free for all com",
                "&7respawns infinitos.",
                "",
                "&b│ &fJogadores: &b" + queueService.getPlayerCountOfGameType("FFA"),
                "",
                this.getLore(profile, QueueType.FFA),
                CC.MENU_BAR
        )));

        int slot = 10;
        switch (profile.getQueueType()) {
            case UNRANKED:
                for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
                    if (!queue.isRanked() && !queue.isDuos() && queue.getKit().getCategory() == KitCategory.NORMAL) {
                        slot = this.skipIfSlotCrossingBorder(slot);
                        buttons.put(slot++, new UnrankedButton(queue));
                    }
                }

                buttons.put(40, new QueueModeSwitcherButton(QueueType.UNRANKED, KitCategory.EXTRA));

                break;
            case BOTS:
                for (Kit kit : HardPractice.getInstance().getService(KitService.class).getKits()) {
                    slot = this.skipIfSlotCrossingBorder(slot);
                    buttons.put(slot++, new BotButton(kit));
                }

                break;
            case DUOS:
                for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
                    if (!queue.isRanked() && queue.isDuos() && queue.getKit().getCategory() == KitCategory.NORMAL) {
                        slot = this.skipIfSlotCrossingBorder(slot);
                        buttons.put(slot++, new UnrankedButton(queue));
                    }
                }

                buttons.put(40, new QueueModeSwitcherButton(QueueType.DUOS, KitCategory.EXTRA));

                break;
            case FFA:
                for (FFAMatch match : HardPractice.getInstance().getService(FFAService.class).getMatches()) {
                    buttons.put(match.getKit().getFfaSlot(), new FFAButton(match));
                }

                break;
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }

    @AllArgsConstructor
    public static class QueuesButtonModern extends Button {
        private String displayName;
        private Material material;
        private int durability;
        private List<String> lore;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.material)
                    .name(this.displayName)
                    .durability(this.durability)
                    .lore(this.lore)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            if (clickType != ClickType.LEFT) return;

            Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
            switch (this.material) {
                case DIAMOND_SWORD:
                    profile.setQueueType(QueueType.UNRANKED);
                    break;
                case GOLD_SWORD:
                    profile.setQueueType(QueueType.DUOS);
                    break;
                case GOLD_AXE:
                    profile.setQueueType(QueueType.FFA);
                    break;
            }

            new QueuesMenuModern().openMenu(player);

            this.playNeutral(player);
        }
    }

    /**
     * Get the lore for the selected queue type.
     *
     * @param profile the player's profile
     * @param type    the queue type to check
     */
    private String getLore(Profile profile, QueueType type) {
        return LoreHelper.selectionLore(profile.getQueueType() == type, "select");
    }
}
