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
            description = "Define várias configurações de explosivos."
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
                "&b&lAjuda dos Comandos de Explosivos:",
                " &f◆ &b/explosive explosion <value> &8- &7Define o alcance de remoção dos blocos da explosão.",
                " &f◆ &b/explosive range <value> &8- &7Define o alcance da explosão que afeta jogadores.",
                " &f◆ &b/explosive horizontal <value> &8- &7Define o knockback horizontal.",
                " &f◆ &b/explosive vertical <value> &8- &7Define o knockback vertical.",
                " &f◆ &b/explosive speed <value> &8- &7Define a velocidade de lançamento da fireball.",
                " &f◆ &b/explosive fuse <value> &8- &7Define os ticks do pavio da TNT.",
                ""
        );

        helpMessage.forEach(line -> sender.sendMessage(CC.translate(line)));
    }
}
