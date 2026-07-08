package live.pvphard.practice.feature.level.command.impl.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.feature.level.data.LevelData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 26/05/2025
 */
public class LevelAdminSetIconCommand extends BaseCommand {
    @CommandData(
            name = "leveladmin.seticon",
            isAdminOnly = true,
            usage = "leveladmin seticon <levelName>",
            description = "Sets the icon of a level"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String levelName = args[0];
        LevelService levelService = this.plugin.getService(LevelService.class);
        LevelData level = levelService.getLevel(levelName);
        if (level == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_NOT_FOUND).replace("{level-name}", levelName));
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        Material iconMaterial = player.getItemInHand().getType();
        int durability = player.getItemInHand().getDurability();
        level.setMaterial(iconMaterial);
        level.setDurability(durability);
        levelService.saveLevel(level);

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_ICON_SET)
                .replace("{level-name}", levelName)
                .replace("{icon-material}", iconMaterial.name())
        );
    }
}