package live.pvphard.practice.feature.cosmetic.command.impl.admin;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.StringUtil;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import live.pvphard.practice.feature.cosmetic.model.Cosmetic;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public class CosmeticListCommand extends BaseCommand {
    @CommandData(
            name = "cosmetic.list",
            isAdminOnly = true,
            usage = "cosmetic list",
            description = "Lista todos os cosméticos registrados."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Map<CosmeticType, BaseCosmeticRepository<?>> repositories = this.plugin.getService(CosmeticService.class).getRepositories();

        player.sendMessage("");

        if (repositories.isEmpty()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.COSMETICS_NONE_REGISTERED));
            player.sendMessage("");
            return;
        }

        for (Map.Entry<CosmeticType, BaseCosmeticRepository<?>> entry : repositories.entrySet()) {
            CosmeticType type = entry.getKey();
            BaseCosmeticRepository<?> repository = entry.getValue();

            List<? extends Cosmetic> cosmetics = repository.getCosmetics();
            if (cosmetics.isEmpty()) {
                continue;
            }

            String friendlyTypeName = StringUtil.formatEnumName(type);
            String header = String.format("     &b&l%s &f(%d)", friendlyTypeName, cosmetics.size());
            player.sendMessage(CC.translate(header));

            for (Cosmetic cosmetic : cosmetics) {
                player.sendMessage(CC.translate("      &f◆ &b" + cosmetic.getName()));
            }
        }

        player.sendMessage("");
    }
}