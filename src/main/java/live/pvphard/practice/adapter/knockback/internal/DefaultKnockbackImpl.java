package live.pvphard.practice.adapter.knockback.internal;

import live.pvphard.practice.adapter.knockback.Knockback;
import live.pvphard.practice.adapter.knockback.KnockbackType;
import org.bukkit.entity.Player;

public class DefaultKnockbackImpl implements Knockback {
    @Override
    public KnockbackType getType() {
        return KnockbackType.DEFAULT;
    }

    @Override
    public void applyKnockback(Player player, String profile) {

    }
}