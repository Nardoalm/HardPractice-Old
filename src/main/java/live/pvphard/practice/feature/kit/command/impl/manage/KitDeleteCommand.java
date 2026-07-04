package live.pvphard.practice.feature.kit.command.impl.manage;

import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.ActionBarReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 20/05/2024
 */
public class KitDeleteCommand extends BaseCommand {
    @CommandData(
            name = "kit.delete",
            isAdminOnly = true,
            usage = "kit delete <kitName>",
            description = "Exclui um kit do servidor"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(kitName);
        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        kitService.deleteKit(kit);
        this.plugin.getService(ArenaService.class).getArenas().forEach(arena -> {
            if (arena.getKits().contains(kitName)) {
                arena.getKits().remove(kitName);
                arena.saveArena();
            }
        });

        String message = this.getString(GlobalMessagesLocaleImpl.KIT_DELETED).replace("{kit-name}", kitName);

        player.sendMessage(message);
        this.plugin.getService(ReflectionService.class).getReflectionService(ActionBarReflectionServiceImpl.class).sendMessage(player, message, 5);
        player.sendMessage("");
        player.sendMessage(CC.translate("&7Não se esqueça de recarregar as filas usando &c&l/queue reload&7."));
        player.sendMessage(CC.translate("&7Além disso, o kit foi removido de todas as arenas onde estava adicionado."));
        player.sendMessage("");
    }
}