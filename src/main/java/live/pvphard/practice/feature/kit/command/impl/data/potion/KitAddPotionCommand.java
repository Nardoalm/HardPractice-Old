package live.pvphard.practice.feature.kit.command.impl.data.potion;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 14/03/2025
 */
public class KitAddPotionCommand extends BaseCommand {
    @CommandData(
            name = "kit.addpotion",
            aliases = {"kit.potion"},
            isAdminOnly = true,
            usage = "kit addpotion <kitName>",
            description = "Adiciona efeitos de poção a um kit com base na poção que você está segurando."
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

        ItemStack itemInHand = player.getInventory().getItemInHand();
        if (itemInHand == null || itemInHand.getType() != Material.POTION) {
            player.sendMessage(CC.translate("&cVocê precisa segurar uma poção para definir efeitos deste kit!"));
            return;
        }

        if (!(itemInHand.getItemMeta() instanceof PotionMeta)) {
            player.sendMessage(CC.translate("&cPoção inválida!"));
            return;
        }

        PotionMeta potionMeta = (PotionMeta) itemInHand.getItemMeta();
        List<PotionEffect> effects = potionMeta.getCustomEffects();
        if (effects.isEmpty()) {
            player.sendMessage(CC.translate("&cA poção que você está segurando não possui efeitos customizados!"));
            return;
        }

        effects.forEach(effect -> kit.getPotionEffects().add(effect));
        kitService.saveKit(kit);
        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_POTION_EFFECTS_SET)).replace("{kit-name}", kit.getName()));
    }
}
