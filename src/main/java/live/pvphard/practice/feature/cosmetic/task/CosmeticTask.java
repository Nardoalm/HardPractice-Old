package live.pvphard.practice.feature.cosmetic.task;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.cosmetic.CosmeticListener;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.CloakRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.cloak.BaseCloak;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Remi
 * @project hard-practice
 * @date 4/08/2025
 */
public class CosmeticTask extends BukkitRunnable {

    private final HardPractice plugin;
    private static final long CLOAK_STILL_DELAY = 1500;

    private static final byte[] RAINBOW_WOOL_COLORS = {14, 1, 4, 5, 13, 9, 3, 11, 10, 2, 6};
    private int currentRainbowIndex = 0;
    private int tickCounter = 0;

    public CosmeticTask(HardPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ProfileService profileService = plugin.getService(ProfileService.class);
        CosmeticService cosmeticService = plugin.getService(CosmeticService.class);
        if (profileService == null || cosmeticService == null) return;

        tickCounter++;
        boolean updateWool = (tickCounter % 5 == 0);
        if (updateWool) {
            currentRainbowIndex = (currentRainbowIndex + 1) % RAINBOW_WOOL_COLORS.length;
        }
        byte currentWoolColor = RAINBOW_WOOL_COLORS[currentRainbowIndex];

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!PlayerUtil.inLobby(player)) continue;

            Profile profile = profileService.getProfile(player.getUniqueId());
            if (profile == null) continue;

            if (CosmeticListener.isPlayerStill(player, CLOAK_STILL_DELAY)) {
                String cloakName = profile.getProfileData().getCosmeticData().getSelected(CosmeticType.CLOAK);
                if (cloakName != null && !cloakName.equalsIgnoreCase("None")) {
                    CloakRepository repo = cosmeticService.getRepository(CosmeticType.CLOAK, CloakRepository.class);
                    if (repo != null) {
                        BaseCloak cloak = repo.getCosmetic(cloakName);
                        if (cloak != null) {
                            cloak.render(player);
                        }
                    }
                }
            }

            if (updateWool) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) return;

                    boolean changed = false;
                    int heldSlot = player.getInventory().getHeldItemSlot();
                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        // Skip the currently held slot to avoid the client equip/hand-bobbing animation
                        if (i == heldSlot) continue;

                        ItemStack item = player.getInventory().getItem(i);
                        if (item != null && item.getType() == Material.WOOL) {
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                                    item.getItemMeta().getDisplayName().contains("Rainbow Blocks")) {
                                item.setDurability(currentWoolColor);
                                item.setAmount(64);
                                changed = true;
                            }
                        }
                    }
                    if (changed) {
                        player.updateInventory();
                    }
                });
            }
        }
    }
}