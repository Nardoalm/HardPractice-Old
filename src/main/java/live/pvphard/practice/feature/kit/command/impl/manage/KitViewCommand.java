package live.pvphard.practice.feature.kit.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.ClickableUtil;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 08/10/2024 - 19:56
 */
public class KitViewCommand extends BaseCommand {
    @CommandData(
            name = "kit.view",
            aliases = "kit.info",
            isAdminOnly = true,
            usage = "kit view <kitName>",
            description = "Ver informações sobre um kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        Kit kit = this.plugin.getService(KitService.class).getKit(args[0]);
        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        player.sendMessage("");
        player.sendMessage(CC.translate("&b&lKit " + kit.getName() + " &f(" + (kit.isEnabled() ? "&aAtivado" : "&cDesativado") + "&f)"));
        player.sendMessage(CC.translate(" &f◆ &bNome de Exibição: &f" + kit.getDisplayName()));
        player.sendMessage(CC.translate(" &f◆ &bName: &f" + kit.getName()));
        player.sendMessage(CC.translate(" &f◆ &bIcon: &f" + kit.getIcon().name().toLowerCase() + " &7(" + kit.getDurability() + ")"));
        player.sendMessage(CC.translate(" &f◆ &bAviso: &f" + kit.getDisclaimer()));
        player.sendMessage(CC.translate(" &f◆ &bDescrição: &f" + kit.getDescription()));
        player.sendMessage(CC.translate(" &f◆ &bFFA: &f" + (kit.isFfaEnabled() ? "&aAtivado" : "&cDesativado")));
        player.spigot().sendMessage(ClickableUtil.createComponent(
                "  &a(Clique aqui para ver as configurações do kit)",
                "/kit viewsettings " + kit.getName(),
                "&7Clique para ver as configurações do kit &b" + kit.getName())
        );
        player.sendMessage("");
    }
}
