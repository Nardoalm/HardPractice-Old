package live.pvphard.practice.feature.abilities;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.time.TimeUtil;
import live.pvphard.practice.common.text.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class Ability implements Listener {
    @Getter
    private static final List<Ability> abilities = Lists.newArrayList();

    private final String ability;
    private Table<String, UUID, Long> cooldown = HashBasedTable.create();

    public Ability(String ability) {
        this.ability = ability;
        abilities.add(this);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, HardPractice.getInstance());
    }

    public boolean isAbility(ItemStack itemStack) {
        AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);
        String displayName = abilityService != null ? abilityService.getDisplayName(ability) : null;
        if (displayName == null || displayName.trim().isEmpty()) {
            return false;
        }

        return (itemStack != null)
                && (itemStack.getType() != Material.AIR)
                && (itemStack.hasItemMeta())
                && (itemStack.getItemMeta().getDisplayName() != null)
                && (itemStack.getItemMeta().getLore() != null)
                && itemStack.getItemMeta().getDisplayName().equals(CC.translate(displayName));
    }

    public String getName() {
        AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);
        String displayName = abilityService != null ? abilityService.getDisplayName(this.getAbility()) : null;
        return displayName != null ? displayName : this.getAbility();
    }

    public boolean hasCooldown(Player player) {
        if (player == null) {
            return false;
        }

        String key = this.getName();
        Long expiresAt = this.cooldown.get(key, player.getUniqueId());
        return expiresAt != null && expiresAt > System.currentTimeMillis();
    }

    public void setCooldown(Player player, long time) {
        if (player == null) {
            return;
        }

        String key = this.getName();
        if (time < 1L) {
            this.cooldown.remove(key, player.getUniqueId());
        }
        else {
            this.cooldown.put(key, player.getUniqueId(), System.currentTimeMillis() + time);
        }
    }

    public String getCooldown(Player player) {
        if (player == null) {
            return TimeUtil.formatLongMin(0L);
        }

        Long expiresAt = this.cooldown.get(this.getName(), player.getUniqueId());
        if (expiresAt == null) {
            return TimeUtil.formatLongMin(0L);
        }

        long cooldownLeft = Math.max(0L, expiresAt - System.currentTimeMillis());
        return TimeUtil.formatLongMin(cooldownLeft);
    }
}
