package live.pvphard.practice.feature.kit.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.serializer.Serializer;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.setting.KitSetting;
import live.pvphard.practice.feature.kit.setting.KitSettingService;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @date 19/05/2024 - 23:30
 */
@Getter
@Service(provides = KitService.class, priority = 80)
public class KitServiceImpl implements KitService {
    private final ConfigService configService;
    private final KitSettingService kitSettingService;
    private final LocaleService localeService;

    private final List<Kit> kits = new ArrayList<>();

    /**
     * DI Constructor for the KitServiceImpl class.
     *
     * @param configService     The configuration service.
     * @param kitSettingService The kit setting service.
     * @param localeService     The locale service.
     */
    public KitServiceImpl(ConfigService configService, KitSettingService kitSettingService, LocaleService localeService) {
        this.configService = configService;
        this.kitSettingService = kitSettingService;
        this.localeService = localeService;
    }

    @Override
    public void initialize(HardContext context) {
        this.loadKits();
    }

    @Override
    public void shutdown(HardContext context) {
        this.saveKits();
        Logger.info("Saved all kits.");
    }

    @Override
    public List<Kit> getKits() {
        return Collections.unmodifiableList(this.kits);
    }

    /**
     * Method to load all kits from the kits.yml file.
     */
    public void loadKits() {
        this.kits.clear();
        FileConfiguration config = this.configService.getKitsConfig();
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");
        if (kitsSection == null) {
            return;
        }

        for (String name : kitsSection.getKeys(false)) {
            String key = "kits." + name;

            try {
                String categoryStr = config.getString(key + ".category");
                KitCategory category = KitCategory.NORMAL;
                if (categoryStr != null) {
                    try {
                        category = KitCategory.valueOf(categoryStr);
                    } catch (IllegalArgumentException e) {
                        Logger.error("Categoria invalida '" + categoryStr + "' para o kit: " + name + ". Usando NORMAL.");
                    }
                }

                String materialStr = config.getString(key + ".icon.material");
                Material icon = Material.DIAMOND_SWORD;
                if (materialStr != null) {
                    Material matched = Material.matchMaterial(materialStr);
                    if (matched != null) {
                        icon = matched;
                    } else {
                        Logger.error("Material de icone invalido '" + materialStr + "' para o kit: " + name + ". Usando DIAMOND_SWORD.");
                    }
                }

                Kit kit = new Kit(
                        name,
                        config.getString(key + ".display-name"),
                        config.getString(key + ".description"),
                        config.getString(key + ".disclaimer"),
                        config.getString(key + ".menu-title"),
                        category,
                        icon,
                        config.getInt(key + ".icon.durability"),
                        Serializer.deserializeItemStack(config.getString(key + ".items")),
                        Serializer.deserializeItemStack(config.getString(key + ".armor")),
                        Serializer.deserializeItemStack(config.getString(key + ".editor-items"))
                );

                kit.setEnabled(config.getBoolean(key + ".enabled"));
                kit.setEditable(config.getBoolean(key + ".editable"));
                kit.setKnockbackProfile(config.getString(key + ".knockback-profile"));

                this.setupFFA(kit, config, key);
                this.loadKitSettings(config, key, kit);

                this.loadPotionEffects(config, key, kit);
                this.addMissingKitSettings(kit, config, key);
                this.kits.add(kit);
            } catch (Exception e) {
                Logger.logException("Falha ao carregar o kit '" + name + "' da configuracao. O kit foi ignorado.", e);
            }
        }
    }

    private void loadKitSettings(FileConfiguration config, String key, Kit kit) {
        ConfigurationSection settingsSection = config.getConfigurationSection(key + ".settings");
        if (settingsSection == null) {
            this.applyDefaultSettings(config, key, kit);
            return;
        }

        for (String settingName : settingsSection.getKeys(false)) {
            boolean enabled = settingsSection.getBoolean(settingName);

            KitSetting kitSetting = this.kitSettingService.createSettingByName(settingName);
            if (kitSetting != null) {
                kitSetting.setEnabled(enabled);
                kit.addKitSetting(kitSetting);
            }
        }
    }

    @Override
    public void saveKit(Kit kit) {
        FileConfiguration config = this.configService.getKitsConfig();
        String key = "kits." + kit.getName();
        this.kitToConfig(kit, config, key);

        if (kit.getKitSettings() == null) {
            this.applyDefaultSettings(config, key, kit);
        } else {
            this.saveKitSettings(config, key, kit);
        }

        if (kit.getPotionEffects() != null) {
            this.savePotionEffects(config, key, kit);
        }

        this.configService.saveConfig(this.configService.getConfigFile("storage/kits.yml"), config);
    }

    @Override
    public void createKit(String kitName, ItemStack[] inventory, ItemStack[] armor, Material icon) {
        String defaultDisplayName = this.localeService.getString(SettingsLocaleImpl.CONFIG_KIT_DEFAULT_DISPLAYNAME).replace("{kit-name}", kitName);
        String defaultDescription = this.localeService.getString(SettingsLocaleImpl.CONFIG_KIT_DEFAULT_DESCRIPTION).replace("{kit-name}", kitName);
        String defaultDisclaimer = this.localeService.getString(SettingsLocaleImpl.CONFIG_KIT_DEFAULT_DISCLAIMER).replace("{kit-name}", kitName);
        String defaultMenuTitle = this.localeService.getString(SettingsLocaleImpl.CONFIG_KIT_DEFAULT_MENU_TITLE).replace("{kit-name}", kitName);

        Kit kit = new Kit(
                kitName,
                defaultDisplayName,
                defaultDescription,
                defaultDisclaimer,
                defaultMenuTitle,
                KitCategory.NORMAL,
                icon,
                0,
                inventory,
                armor,
                new ItemStack[0]
        );

        kitSettingService.applyAllSettingsToKit(kit);
        this.kits.add(kit);
        this.saveKit(kit);
    }

    @Override
    public void deleteKit(Kit kit) {
        FileConfiguration config = this.configService.getKitsConfig();

        this.kits.remove(kit);
        config.set("kits." + kit.getName(), null);

        this.configService.saveConfig(this.configService.getConfigFile("storage/kits.yml"), config);
    }

    @Override
    public Kit getKit(String name) {
        return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void saveKits() {
        for (Kit kit : this.kits) {
            FileConfiguration config = configService.getKitsConfig();
            String key = "kits." + kit.getName();
            this.kitToConfig(kit, config, key);
            this.saveKitSettings(config, key, kit);
            this.savePotionEffects(config, key, kit);
            configService.saveConfig(configService.getConfigFile("storage/kits.yml"), config);
        }
    }

    /**
     * Method to get a kit by its object.
     *
     * @param kit The kit instance.
     * @return The kit.
     */
    public Kit getKit(Kit kit) {
        return this.kits.stream().filter(k -> k.getName().equalsIgnoreCase(kit.getName())).findFirst().orElse(null);
    }

    /**
     * Method to save the settings of a kit.
     *
     * @param config The configuration file.
     * @param key    The path key.
     * @param kit    The kit.
     */
    private void saveKitSettings(FileConfiguration config, String key, Kit kit) {
        for (KitSetting kitSetting : kit.getKitSettings()) {
            config.set(key + ".settings." + kitSetting.getName(), kitSetting.isEnabled());
        }
    }


    /**
     * Method to load the potion effects of a kit.
     *
     * @param config The configuration file.
     * @param key    The path key.
     * @param kit    The kit.
     */
    private void loadPotionEffects(FileConfiguration config, String key, Kit kit) {
        try {
            List<PotionEffect> potionEffects = Serializer.deserializePotionEffects(config.getStringList(key + ".potion-effects"));
            kit.setPotionEffects(potionEffects);
        } catch (Exception exception) {
            Logger.logException("Falha ao carregar os efeitos de poção do kit " + kit.getName() + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Handle creation in config for each kit that has missing settings (for development purposes).
     *
     * @param kit    The kit.
     * @param config The configuration file.
     * @param key    The path key.
     */
    private void addMissingKitSettings(Kit kit, FileConfiguration config, String key) {
        this.kitSettingService.getSettings().forEach(setting -> {
            if (kit.getKitSettings().stream().noneMatch(kitSetting -> kitSetting.getName().equals(setting.getName()))) {
                kit.addKitSetting(setting);
                Logger.info("&cAdded missing kit setting to &4" + kit.getName() + ": &c" + setting.getName());
                this.saveKitSettings(config, key, kit);
            }
        });
    }

    /**
     * Method to apply the default settings to a kit.
     *
     * @param config The configuration file.
     * @param key    The path key.
     * @param kit    The kit.
     */
    public void applyDefaultSettings(FileConfiguration config, String key, Kit kit) {
        kitSettingService.getSettings().forEach(setting -> {
            kit.addKitSetting(setting);
            config.set(key + ".settings." + setting.getName(), setting.isEnabled());
        });

        configService.saveConfig(configService.getConfigFile("storage/kits.yml"), config);
    }

    /**
     * Method to set up the FFA settings of a kit.
     *
     * @param kit    The kit.
     * @param config The configuration file.
     * @param key    The path key.
     */
    private void setupFFA(Kit kit, FileConfiguration config, String key) {
        kit.setFfaEnabled(config.getBoolean(key + ".ffa.enabled"));
        kit.setFfaArenaName(config.getString(key + ".ffa.arena-name"));
        kit.setMaxFfaPlayers(config.getInt(key + ".ffa.max-players"));
        kit.setFfaSlot(config.getInt(key + ".ffa.slot"));
    }

    /**
     * Method to save the potion effects of a kit.
     *
     * @param config The configuration file.
     * @param key    The path key.
     * @param kit    The kit.
     */
    private void savePotionEffects(FileConfiguration config, String key, Kit kit) {
        List<String> potionEffects = Serializer.serializePotionEffects(kit.getPotionEffects());
        config.set(key + ".potion-effects", potionEffects);
    }

    /**
     * Method to save a kit to the configuration specified in the parameters with a given key path.
     *
     * @param kit    The kit to save.
     * @param config The configuration file.
     * @param key    The path key.
     */
    private void kitToConfig(Kit kit, FileConfiguration config, String key) {
        config.set(key + ".display-name", kit.getDisplayName());
        config.set(key + ".description", kit.getDescription());
        config.set(key + ".disclaimer", kit.getDisclaimer());
        config.set(key + ".menu-title", kit.getMenuTitle());
        config.set(key + ".enabled", kit.isEnabled());
        config.set(key + ".editable", kit.isEditable());
        config.set(key + ".category", kit.getCategory().name());
        config.set(key + ".icon.material", kit.getIcon().name());
        config.set(key + ".icon.durability", kit.getDurability());
        config.set(key + ".ffa.arena-name", kit.getFfaArenaName());
        config.set(key + ".ffa.enabled", kit.isFfaEnabled());
        config.set(key + ".ffa.slot", kit.getFfaSlot());
        config.set(key + ".ffa.max-players", kit.getMaxFfaPlayers());
        config.set(key + ".items", Serializer.serializeItemStack(kit.getItems()));
        config.set(key + ".armor", Serializer.serializeItemStack(kit.getArmor()));
        config.set(key + ".knockback-profile", kit.getKnockbackProfile());
        config.set(key + ".editor-items", Serializer.serializeItemStack(kit.getEditorItems()));
    }
}
