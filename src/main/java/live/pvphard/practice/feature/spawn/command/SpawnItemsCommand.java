package live.pvphard.practice.feature.spawn.command;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 29/04/2024 - 18:45
 */
public class SpawnItemsCommand extends BaseCommand {
    @CommandData(
            name = "spawnitems",
            aliases = {"lobbyitems"},
            isAdminOnly = true,
            usage = "spawnitems",
            description = "Gives the player the spawn items."
    )
    @Override
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        this.plugin.getService(HotbarService.class).applyHotbarItems(player);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.SPAWN_ITEMS_GIVEN));
    }
}