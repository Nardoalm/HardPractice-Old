package live.pvphard.practice.feature.abilities.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.TaskUtil;
import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.AbilityService;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Service(provides = AbilityService.class, priority = 380)
public class AbilityServiceImpl implements AbilityService {
    private final ConfigService configService;
    private final PluginConstant pluginConstant;

    private final Set<Ability> abilities = new HashSet<>();

    public AbilityServiceImpl(ConfigService configService, PluginConstant pluginConstant) {
        this.configService = configService;
        this.pluginConstant = pluginConstant;
    }

    @Override
    public void initialize(HardContext context) {
        this.registerAbilities();

        Ability.getAbilities().forEach(Ability::register);
    }

    private void registerAbilities() {
        Reflections reflections = this.pluginConstant.getReflections();

        for (Class<? extends Ability> clazz : reflections.getSubTypesOf(Ability.class)) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            try {
                Ability instance = clazz.getDeclaredConstructor().newInstance();
                this.abilities.add(instance);
            } catch (Exception e) {
                Logger.logException("Falha ao instanciar a ability: " + clazz.getName(), e);
            }
        }
    }

    @Override
    public <T extends Ability> T getAbility(Class<T> abilityClass) {
        return this.abilities.stream()
                .filter(abilityClass::isInstance)
                .map(abilityClass::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ItemStack getAbilityItem(String abilityKey, int amount) {
        return new ItemBuilder(getMaterial(abilityKey))
                .amount(amount)
                .durability(getData(abilityKey))
                .name(getDisplayName(abilityKey))
                .lore(getDescription(abilityKey))
                .build();
    }

    @Override
    public String getDisplayName(String abilityKey) {
        String displayName = configService.getAbilityConfig().getString(abilityKey + ".ICON.DISPLAYNAME");
        return displayName != null ? displayName : abilityKey;
    }

    @Override
    public List<String> getDescription(String abilityKey) {
        List<String> description = configService.getAbilityConfig().getStringList(abilityKey + ".ICON.DESCRIPTION");
        return description != null ? description : Collections.emptyList();
    }

    public Material getMaterial(String ability) {
        String materialName = configService.getAbilityConfig().getString(ability + ".ICON.MATERIAL");
        if (materialName == null) {
            return Material.PAPER;
        }

        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return Material.PAPER;
        }
    }

    public int getData(String ability) {
        return configService.getAbilityConfig().getInt(ability + ".ICON.DATA");
    }

    public int getCooldown(String ability) {
        return configService.getAbilityConfig().getInt(ability + ".COOLDOWN");
    }

    @Override
    public Set<String> getAbilityKeys() {
        if (configService.getAbilityConfig().getConfigurationSection("") == null) {
            return Collections.emptySet();
        }
        return configService.getAbilityConfig().getConfigurationSection("").getKeys(false);
    }

    @Override
    public void giveAbility(CommandSender sender, Player player, String key, String abilityName, int amount) {
        player.getInventory().addItem(this.getAbilityItem(key, amount));
        if (player == sender) {
            player.sendMessage(CC.translate(configService.getAbilityConfig().getString("RECEIVED_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount))));
        } else {
            player.sendMessage(CC.translate(configService.getAbilityConfig().getString("RECEIVED_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount))));
            sender.sendMessage(CC.translate(configService.getAbilityConfig().getString("GIVE_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount))
                    .replace("%PLAYER%", player.getName())));
        }
    }

    @Override
    public void sendPlayerMessage(Player player, String abilityKey) {
        if (player == null) {
            return;
        }

        String displayName = getDisplayName(abilityKey);
        String cooldown = String.valueOf(getCooldown(abilityKey));

        configService.getAbilityConfig().getStringList(abilityKey + ".MESSAGE.PLAYER").forEach(
                message -> CC.message(player, message
                        .replace("%ABILITY%", displayName)
                        .replace("%COOLDOWN%", cooldown)));
    }

    @Override
    public void sendTargetMessage(Player target, Player player, String abilityKey) {
        if (target == null || player == null) {
            return;
        }

        String displayName = getDisplayName(abilityKey);

        configService.getAbilityConfig().getStringList(abilityKey + ".MESSAGE.TARGET").forEach(
                message -> CC.message(target, message
                        .replace("%ABILITY%", displayName)
                        .replace("%PLAYER%", player.getName())
                        .replace("%TARGET%", target.getName())));
    }

    @Override
    public void sendCooldownMessage(Player player, String abilityName, String cooldown) {
        if (player == null) {
            return;
        }

        String message = configService.getAbilityConfig().getString("STILL_ON_COOLDOWN");
        if (message == null) {
            message = "&c%ABILITY% em cooldown por %COOLDOWN%.";
        }

        CC.message(player, message
                .replace("%ABILITY%", abilityName != null ? abilityName : "Ability")
                .replace("%COOLDOWN%", cooldown != null ? cooldown : "0s"));
    }

    @Override
    public void sendCooldownExpiredMessage(Player player, String abilityName, String ability) {
        if (player == null) {
            return;
        }

        String rawMessage = configService.getAbilityConfig().getString("COOLDOWN_EXPIRED");
        if (rawMessage == null) {
            rawMessage = "&a%ABILITY% pronto para uso novamente.";
        }

        final String finalMessage = rawMessage;
        final String finalAbilityName = abilityName != null ? abilityName : "Ability";

        TaskUtil.runLaterAsync(() ->
                CC.message(player, finalMessage
                        .replace("%ABILITY%", finalAbilityName)), getCooldown(ability) * 20L);
    }
}
