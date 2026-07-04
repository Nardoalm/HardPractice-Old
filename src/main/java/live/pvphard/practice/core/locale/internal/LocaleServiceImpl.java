package live.pvphard.practice.core.locale.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleEntry;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 09/09/2025
 */
@Service(provides = LocaleService.class, priority = 25)
public class LocaleServiceImpl implements LocaleService {
    private final ConfigService configService;

    /**
     * DI Constructor for the LocaleServiceImpl class.
     *
     * @param configService the ConfigService instance.
     */
    public LocaleServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void initialize(HardContext context) {
        List<Class<? extends LocaleEntry>> localeEntries = Arrays.asList(
                GlobalMessagesLocaleImpl.class,
                GameMessagesLocaleImpl.class,
                SettingsLocaleImpl.class,
                VisualsLocaleImpl.class
        );

        Logger.infoNoPrefix("");
        Logger.info("Verificando entradas de locale...");
        Logger.infoNoPrefix("");

        int missingCount = 0;

        for (Class<? extends LocaleEntry> localeClass : localeEntries) {
            Map<String, Boolean> filesToSave = new HashMap<>();

            for (LocaleEntry entry : localeClass.getEnumConstants()) {
                FileConfiguration config = this.configService.getConfig(entry.getConfigName());
                if (!config.contains(entry.getConfigPath())) {
                    config.set(entry.getConfigPath(), entry.getDefaultValue());
                    Logger.infoNoPrefix("&8'&b" + entry.getConfigPath() + "&8' &fnão encontrada em &b" + entry.getConfigName() + "&f.");
                    filesToSave.put(entry.getConfigName(), true);
                    missingCount++;
                }
            }

            for (String fileName : filesToSave.keySet()) {
                File file = this.configService.getConfigFile(fileName);
                FileConfiguration config = this.configService.getConfig(fileName);
                this.configService.saveConfig(file, config);
            }
        }

        Logger.infoNoPrefix("");
        Logger.info("Verificação de locales concluída.");
        if (missingCount > 0) {
            Logger.info("Total de " + missingCount + " entrad" + (missingCount == 1 ? "a" : "as") + " de locale ausente" + (missingCount == 1 ? "" : "s") + " adicionad" + (missingCount == 1 ? "a" : "as") + ".");
        } else {
            Logger.info("Nenhuma entrada de locale ausente foi encontrada.");
        }
        Logger.infoNoPrefix("");
    }

    @Override
    public String getString(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return CC.translate(config.getString(entry.getConfigPath()));
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return CC.translate(entry.getDefaultValue().toString());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getStringList(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return CC.translateList(config.getStringList(entry.getConfigPath()));
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return CC.translateList((List<String>) entry.getDefaultValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getStringListRaw(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return config.getStringList(entry.getConfigPath());
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return (List<String>) entry.getDefaultValue();
        }
    }

    @Override
    public int getInt(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return config.getInt(entry.getConfigPath());
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return (int) entry.getDefaultValue();
        }
    }

    @Override
    public double getDouble(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return config.getDouble(entry.getConfigPath());
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return (double) entry.getDefaultValue();
        }
    }

    @Override
    public boolean getBoolean(LocaleEntry entry) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        if (config.contains(entry.getConfigPath())) {
            return config.getBoolean(entry.getConfigPath());
        } else {
            Logger.error("'" + entry.getConfigPath() + "' não existe em " + entry.getConfigName() + ". Usando valor padrão. Reiniciar o servidor corrigirá isso.");
            return (boolean) entry.getDefaultValue();
        }
    }

    @Override
    public void setString(LocaleEntry entry, String message) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        config.set(entry.getConfigPath(), message);
        File file = this.configService.getConfigFile(entry.getConfigName());
        this.configService.saveConfig(file, config);
    }

    @Override
    public void setList(LocaleEntry entry, List<String> messages) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        config.set(entry.getConfigPath(), messages);
        File file = this.configService.getConfigFile(entry.getConfigName());
        this.configService.saveConfig(file, config);
    }

    @Override
    public void setInt(LocaleEntry entry, int value) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        config.set(entry.getConfigPath(), value);
        File file = this.configService.getConfigFile(entry.getConfigName());
        this.configService.saveConfig(file, config);
    }

    @Override
    public void setDouble(LocaleEntry entry, double value) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        config.set(entry.getConfigPath(), value);
        File file = this.configService.getConfigFile(entry.getConfigName());
        this.configService.saveConfig(file, config);
    }

    @Override
    public void setBoolean(LocaleEntry entry, boolean value) {
        FileConfiguration config = this.configService.getConfig(entry.getConfigName());
        config.set(entry.getConfigPath(), value);
        File file = this.configService.getConfigFile(entry.getConfigName());
        this.configService.saveConfig(file, config);
    }
}
