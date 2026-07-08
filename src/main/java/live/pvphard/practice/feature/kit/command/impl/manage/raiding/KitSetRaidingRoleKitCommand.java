package live.pvphard.practice.feature.kit.command.impl.manage.raiding;

import live.pvphard.practice.common.text.EnumFormatter;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.raiding.BaseRaidingService;
import live.pvphard.practice.feature.kit.setting.KitSettingService;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRaiding;
import live.pvphard.practice.feature.match.model.BaseRaiderRole;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @since 16/06/2025
 */
public class KitSetRaidingRoleKitCommand extends BaseCommand {
    @CommandData(
            name = "kit.setraidingrolekit",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit setraidingrolekit <kitName> <role> <kitName>",
            description = "Sets the raiding role kit for a specific role in a raiding kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 3) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(kitName);
        if (kit == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND));
            return;
        }

        if (!kit.isSettingEnabled(KitSettingRaiding.class)) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_SETTING_NOT_ENABLED)
                    .replace("{kit-name}", kit.getName())
                    .replace("{setting-name}", plugin.getService(KitSettingService.class).getSettingByClass(KitSettingRaiding.class).getName()));
            return;
        }

        String roleName = args[1].toUpperCase();
        BaseRaiderRole role;
        try {
            role = BaseRaiderRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(EnumFormatter.outputAvailableValues(BaseRaiderRole.class));
            return;
        }

        String roleKitName = args[2];
        Kit roleKit = kitService.getKit(roleKitName);
        if (roleKit == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND));
            return;
        }

        if (roleKit.isEnabled()) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_CANNOT_SET_ENABLED_AS_RAIDING_ROLE_KIT).replace("{role-kit}", roleKit.getName()));
            return;
        }

        BaseRaidingService raidingService = this.plugin.getService(BaseRaidingService.class);
        raidingService.setRaidingKitMapping(kit, role, roleKit);
        sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_SET_RAIDING_ROLE_KIT)
                .replace("{role}", role.name())
                .replace("{role-kit}", roleKit.getName())
                .replace("{kit-name}", kit.getName())
        );
    }
}