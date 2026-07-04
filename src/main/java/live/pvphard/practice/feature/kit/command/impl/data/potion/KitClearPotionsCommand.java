package live.pvphard.practice.feature.kit.command.impl.data.potion;

import live.pvphard.practice.common.text.CC;
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
 * @since 16/06/2025
 */
public class KitClearPotionsCommand extends BaseCommand {
    @CommandData(
            name = "kit.clearpotions",
            isAdminOnly = true,
            usage = "kit clearpotions <kitName>",
            description = "Remover todos os efeitos de poção de um kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(args[0]);

        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        if (kit.getPotionEffects().isEmpty()) {
            player.sendMessage(CC.translate("&cThis kit has no potion effects to remove."));
            return;
        }

        kit.getPotionEffects().clear();
        kitService.saveKit(kit);
        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_POTION_EFFECTS_CLEARED).replace("{kit-name}", kit.getName())));
    }
}
