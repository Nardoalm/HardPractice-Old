package live.pvphard.practice.common.logger;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.adapter.knockback.KnockbackAdapter;
import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.feature.emoji.EmojiService;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRanked;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.feature.title.TitleService;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
@UtilityClass
public class PluginLogger {
    /**
     * Send a message to the console when the bootstrap is enabled.
     *
     * @param timeTaken The time taken to enable the bootstrap.
     */
    public void onEnable(long timeTaken) {
        HardPractice plugin = HardPractice.getInstance();
        Server server = plugin.getServer();

        PluginConstant constants = plugin.getService(PluginConstant.class);
        CoreAdapter coreAdapter = plugin.getService(CoreAdapter.class);
        KnockbackAdapter knockbackAdapter = plugin.getService(KnockbackAdapter.class);
        KitService kitService = plugin.getService(KitService.class);
        FFAService ffaService = plugin.getService(FFAService.class);
        ArenaService arenaService = plugin.getService(ArenaService.class);
        DivisionService divisionService = plugin.getService(DivisionService.class);
        TitleService titleService = plugin.getService(TitleService.class);
        LevelService levelService = plugin.getService(LevelService.class);
        EmojiService emojiService = plugin.getService(EmojiService.class);
        LocaleService localeService = plugin.getService(LocaleService.class);
        ProfileService profileService = plugin.getService(ProfileService.class);

        ChatColor color = constants.getMainColor();
        ChatColor secondary = ChatColor.WHITE;

        Arrays.asList(
                "",
                "        " + color + "&l" + constants.getName() + color + " Practice",
                "",
               "",
                "    " + secondary + "Versão: " + color + constants.getVersion(),
                "    " + secondary + "Descrição: " + color + constants.getDescription(),
                "",
                "    " + secondary + "Core conectado: " + color + coreAdapter.getCore().getType().getPluginName(),
                "    " + secondary + "Knockback conectado: " + color + knockbackAdapter.getKnockbackImplementation().getType().getSpigotName(),
                "",
                "    " + secondary + "Arenas: " + color + arenaService.getArenas().size(),
                "    " + secondary + "Divisões: " + color + divisionService.getDivisions().size(),
                "    " + secondary + "Kits Unranked: " + color + kitService.getKits().size(),
                "    " + secondary + "Kits Ranked: " + color + kitService.getKits().stream().filter(Kit::isEnabled).filter(kit -> kit.isSettingEnabled(KitSettingRanked.class)).count(),
                "    " + secondary + "Kits FFA: " + color + ffaService.getFfaKits().size(),
                "    " + secondary + "Níveis: " + color + levelService.getLevels().size(),
                "    " + secondary + "Títulos: " + color + titleService.getTitles().size(),
                "",
                "    " + color + "Essenciais",
                "     " + secondary + "Emojis: " + (emojiService.isEnabled() ? "&aAtivado" : "&cDesativado"),
                "",
                "    " + color + "Banco de Dados",
                "     " + secondary + "Tipo: " + color + profileService.getDatabaseProfile().getType(),
                //"     " + secondary + "Database: " + color + localeService.getMessage(SettingsLocaleImpl.MONGO_CREDENTIALS_DATABASE),
                "",
                "    " + secondary + "Spigot: " + color + server.getName(),
                "    " + secondary + "Versão: " + color + constants.getSpigotVersion(),
                "",
                "    " + secondary + "Carregado em " + color + timeTaken + "ms",
                ""
        ).forEach(line -> server.getConsoleSender().sendMessage(CC.translate(line)));
    }

    public void onDisable() {
        HardPractice plugin = HardPractice.getInstance();
        Arrays.asList(
                "",
                CC.PREFIX + "&cDesativado.",
                ""
        ).forEach(line -> plugin.getServer().getConsoleSender().sendMessage(CC.translate(line)));
    }
}
