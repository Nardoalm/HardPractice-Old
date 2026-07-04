package live.pvphard.practice.feature.party.menu.duel.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.duel.menu.DuelRequestMenu;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class DuelOtherPartyButton extends Button {
    private final HardPractice plugin = HardPractice.getInstance();
    private final Party party;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = getLore();

        ItemStack itemStack = new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (short) 3))
                .name("&b&l" + party.getLeader().getName() + "'s Party")
                .lore(lore)
                .build();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        Player leader = Bukkit.getPlayer(party.getLeader().getName());
        if (leader != null) {
            meta.setOwner(leader.getName());
        }
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private @NotNull List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add(" &bMembers: &f(" + party.getMembers().size() + ")");
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                lore.add("&b│ &f" + member.getName());
            }
        }
        lore.add("");
        lore.add("&aClick to duel this party.");
        lore.add(CC.MENU_BAR);
        return lore;
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        Party playerParty = profile.getParty();

        if (playerParty == null) {
            player.sendMessage(CC.translate("&cYou are not in a party."));
            return;
        }

        if (!playerParty.isLeader(player)) {
            player.sendMessage(CC.translate("&cYou need to be the leader of your party to challenge another party."));
            return;
        }

        if (party.getLeader().equals(player)) {
            player.sendMessage(CC.translate("&cYou cannot duel your own party."));
            return;
        }

        Player targetLeader = Bukkit.getPlayer(party.getLeader().getUniqueId());
        if (targetLeader == null) {
            player.sendMessage(CC.translate("&cThe leader of that party is not online."));
            return;
        }

        player.closeInventory();
        new DuelRequestMenu(targetLeader).openMenu(player);
    }
}