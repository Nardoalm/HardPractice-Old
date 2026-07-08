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
public class KitRemoveRaidingRoleKitCommand extends BaseCommand {
    @CommandData(
            name = "kit.removeraidingrolekit",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit removeraidingrolekit <kitName> <role> <roleKitName>",
            description = "Removes the raiding role kit mapping from a kit."
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
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_NEED_TO_DISABLE_TO_SET_RAIDING_ROLE).replace("{kit-name}", roleKit.getName()));
            return;
        }

        BaseRaidingService raidingService = this.plugin.getService(BaseRaidingService.class);

        if (raidingService.getRaidingKitByRole(kit, role) == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_RAIDING_ROLE_KIT_NOT_MAPPED)
                    .replace("{kit-name}", kit.getName())
                    .replace("{role}", role.name()));
            return;
        }

        raidingService.removeRaidingKitMapping(kit, role);
        sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_RAIDING_ROLE_KIT_REMOVED)
                .replace("{kit-name}", kit.getName())
                .replace("{role}", role.name())
                .replace("{role-kit-name}", roleKit.getName()));
    }
}