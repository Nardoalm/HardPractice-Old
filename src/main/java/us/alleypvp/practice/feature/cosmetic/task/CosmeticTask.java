package us.alleypvp.practice.feature.cosmetic.task;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.PlayerUtil;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.feature.cosmetic.CosmeticListener;
import us.alleypvp.practice.feature.cosmetic.CosmeticService;
import us.alleypvp.practice.feature.cosmetic.internal.repository.CloakRepository;
import us.alleypvp.practice.feature.cosmetic.internal.repository.impl.cloak.BaseCloak;
import us.alleypvp.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Remi
 * @project kaos-practice
 * @date 4/08/2025
 */
public class CosmeticTask extends BukkitRunnable {

    private final AlleyPractice plugin;
    private static final long CLOAK_STILL_DELAY = 1500;

    private byte currentWoolColor = 0;
    private int tickCounter = 0;

    public CosmeticTask(AlleyPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ProfileService profileService = plugin.getService(ProfileService.class);
        CosmeticService cosmeticService = plugin.getService(CosmeticService.class);
        if (profileService == null || cosmeticService == null) return;

        // Since the task runs every 4 ticks, update wool color every 5 executions (20 ticks / 1 second)
        tickCounter++;
        boolean updateWool = (tickCounter % 5 == 0);
        if (updateWool) {
            currentWoolColor = (byte) ((currentWoolColor + 1) % 16);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!PlayerUtil.inLobby(player)) continue;

            Profile profile = profileService.getProfile(player.getUniqueId());
            if (profile == null) continue;

            // Handle Cloak Rendering
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

            // Handle Dynamic Wool Update
            if (updateWool) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Update only happens safely on the main thread
                    if (!player.isOnline()) return;

                    ItemStack inSlot = player.getInventory().getItem(3);
                    if (inSlot != null && inSlot.getType() == Material.WOOL) {
                        inSlot.setDurability(currentWoolColor);
                        inSlot.setAmount(64);
                    }
                });
            }
        }
    }
}