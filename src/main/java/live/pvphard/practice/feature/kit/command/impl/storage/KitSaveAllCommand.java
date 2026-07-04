package live.pvphard.practice.feature.kit.command.impl.storage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 23/05/2024 - 01:18
 */
public class KitSaveAllCommand extends BaseCommand {
    @CommandData(
            name = "kit.saveall",
            isAdminOnly = true,
            usage = "kit saveall",
            description = "Salva todos os kits no armazenamento."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        this.plugin.getService(KitService.class).saveKits();
        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_SAVED_ALL)));
    }
}