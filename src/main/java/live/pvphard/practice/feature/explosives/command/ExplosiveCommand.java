package live.pvphard.practice.feature.explosives.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.explosives.ExplosiveService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @since 24/06/2025
 */
public class ExplosiveCommand extends BaseCommand {
    @CommandData(
            name = "explosive",
            aliases = {"expl"},
            isAdminOnly = true,
            inGameOnly = false,
            usage = "explosive <setting> <value>",
            description = "Sets various explosives settings."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            sendHelpMessage(sender);
            return;
        }

        String settingName = args[0].toLowerCase();
        String valueStr = args[1];
        double value;

        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException exception) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[1]));
            return;
        }

        ExplosiveService explosiveService = this.plugin.getService(ExplosiveService.class);

        switch (settingName) {
            case "horizontal":
                explosiveService.setHorizontalFireballKnockback(value);
                break;
            case "vertical":
                explosiveService.setVerticalFireballKnockback(value);
                break;
            case "range":
                explosiveService.setFireballExplosionRange(value);
                break;
            case "speed":
                explosiveService.setFireballThrowSpeed(value);
                break;
            case "fuse":
                explosiveService.setTntFuseTicks((int) value);
                break;
            case "explosion":
                explosiveService.setTntExplosionRange(value);
                break;
            default:
                sendHelpMessage(sender);
                return;
        }

        explosiveService.save();

        LocaleService localeService = this.plugin.getService(LocaleService.class);
        sender.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.EXPLOSIVE_SETTING_UPDATED)
                .replace("{setting-name}", settingName)
                .replace("{setting-value}", String.valueOf(value))
        );
    }

    private void sendHelpMessage(CommandSender sender) {
        List<String> helpMessage = Arrays.asList(
                "",
                "&b&lExplosives Command Help:",
                " &f◆ &b/explosive explosion <value> &8- &7Sets the block damage range of the explosion.",
                " &f◆ &b/explosive range <value> &8- &7Sets the explosion range that affects players.",
                " &f◆ &b/explosive horizontal <value> &8- &7Sets the horizontal knockback.",
                " &f◆ &b/explosive vertical <value> &8- &7Sets the vertical knockback.",
                " &f◆ &b/explosive speed <value> &8- &7Sets the fireball throw speed.",
                " &f◆ &b/explosive fuse <value> &8- &7Sets the TNT fuse ticks.",
                ""
        );

        helpMessage.forEach(line -> sender.sendMessage(CC.translate(line)));
    }
}
