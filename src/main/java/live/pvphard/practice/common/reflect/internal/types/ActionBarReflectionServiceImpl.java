package live.pvphard.practice.common.reflect.internal.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.reflect.Reflection;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarReflectionServiceImpl implements Reflection {

    public void sendMessage(Player player, String message, int durationSeconds) {
        try {
            if (!shouldSendActionBar(player)) {
                return;
            }

            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(message) + "\"}");
            PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
            this.sendPacket(player, packet);

            if (durationSeconds > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!shouldSendActionBar(player)) {
                            return;
                        }

                        IChatBaseComponent clearChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}");
                        PacketPlayOutChat clearPacket = new PacketPlayOutChat(clearChatBaseComponent, (byte) 2);
                        sendPacket(player, clearPacket);
                    }
                }.runTaskLater(HardPractice.getInstance(), durationSeconds * 20L);
            }
        } catch (Exception exception) {
            String name = player != null ? player.getName() : "unknown-player";
            Logger.logException("An error occurred while trying to send an action bar message to " + name, exception);
        }
    }

    public void sendMessage(Player player, String message) {
        try {
            if (!shouldSendActionBar(player)) {
                return;
            }

            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(message) + "\"}");
            PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
            this.sendPacket(player, packet);
        } catch (Exception exception) {
            String name = player != null ? player.getName() : "unknown-player";
            Logger.logException("An error occurred while trying to send an action bar message to " + name, exception);
        }
    }

    public void sendDeathMessage(Player killer, Player victim) {
        if (killer == null || victim == null) {
            return;
        }

        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);
        if (localeService == null) {
            return;
        }

        if (localeService.getBoolean(VisualsLocaleImpl.ACTIONBAR_DEATH_MESSAGE_ENABLED_BOOLEAN)) {
            Profile victimProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(victim.getUniqueId());
            Profile killerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(killer.getUniqueId());

            String victimColor = victimProfile != null && victimProfile.getNameColor() != null
                    ? victimProfile.getNameColor().toString()
                    : "";
            String killerColor = killerProfile != null && killerProfile.getNameColor() != null
                    ? killerProfile.getNameColor().toString()
                    : "";

            String victimName = PlayerDisplayUtil.resolveDisplayName(victim, victim.getName());
            String killerName = PlayerDisplayUtil.resolveDisplayName(killer, killer.getName());

            String deathMessage = localeService.getString(VisualsLocaleImpl.ACTIONBAR_DEATH_MESSAGE_FORMAT)
                    .replace("{victim}", victimName)
                    .replace("{killer}", killerName)
                    .replace("{name-color}", victimColor)
                    .replace("{victim-name-color}", victimColor)
                    .replace("{victim-color}", victimColor)
                    .replace("{killer-name-color}", killerColor)
                    .replace("{killer-color}", killerColor);
            this.sendMessage(killer, deathMessage, 3);
        }
    }

    public void visualizeTargetHealth(Player player, Player target) {
        if (player == null || target == null) {
            return;
        }

        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);
        if (localeService == null) {
            return;
        }

        Profile targetProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(target.getUniqueId());
        String targetNameColor = targetProfile != null && targetProfile.getNameColor() != null
                ? targetProfile.getNameColor().toString()
                : "";
        String targetName = PlayerDisplayUtil.resolveDisplayName(target, target.getName());

        String message = localeService.getString(VisualsLocaleImpl.ACTIONBAR_HEALTH_INDICATOR_MESSAGE_FORMAT)
                .replace("{target}", targetName)
                .replace("{name-color}", targetNameColor);

        String symbol = localeService.getString(VisualsLocaleImpl.ACTIONBAR_HEALTH_INDICATOR_SYMBOL_APPEARANCE);
        String fullColor = localeService.getString(VisualsLocaleImpl.ACTIONBAR_HEALTH_INDICATOR_SYMBOL_COLOR_FULL);
        String halfColor = localeService.getString(VisualsLocaleImpl.ACTIONBAR_HEALTH_INDICATOR_SYMBOL_COLOR_HALF);
        String emptyColor = localeService.getString(VisualsLocaleImpl.ACTIONBAR_HEALTH_INDICATOR_SYMBOL_COLOR_EMPTY);

        int maxHealth = (int) target.getMaxHealth() / 2;
        double rawHealth = target.getHealth() / 2;
        int currentHealth = (int) Math.ceil(rawHealth);

        StringBuilder healthBar = new StringBuilder();
        for (int i = 0; i < maxHealth; i++) {
            if (i < currentHealth) {
                healthBar.append(CC.translate(fullColor + symbol));
            } else if (i == currentHealth && rawHealth % 1 != 0) {
                healthBar.append(CC.translate(halfColor + symbol));
            } else {
                healthBar.append(CC.translate(emptyColor + symbol));
            }
        }

        String finalMessage = CC.translate(message.replace("{health-bar}", healthBar.toString()));
        this.sendMessage(player, finalMessage, 2);
    }

    private boolean shouldSendActionBar(Player player) {
        if (player == null || !player.isOnline() || PlayerDisplayUtil.isVanished(player)) {
            return false;
        }
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        return profile != null && profile.getState() == ProfileState.PLAYING;
    }
}