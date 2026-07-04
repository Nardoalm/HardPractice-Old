package live.pvphard.practice.feature.kit.command.helper.impl;

import live.pvphard.practice.common.PotionUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Emmy
 * @project Hard
 * @date 03/11/2024 - 20:28
 */
public class PotionDurationCommand extends BaseCommand {
    @CommandData(
            name = "potionduration",
            isAdminOnly = true,
            usage = "potionduration <duration/infinite>",
            description = "Define a duração da poção que você está segurando."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null || itemInHand.getType() != Material.POTION) {
            player.sendMessage(CC.translate("&cVocê precisa estar segurando uma poção."));
            return;
        }

        PotionMeta potionMeta = (PotionMeta) itemInHand.getItemMeta();
        if (potionMeta == null) {
            player.sendMessage(CC.translate("&cA poção não possui efeitos para modificar."));
            return;
        }

        if (args[0].equalsIgnoreCase("infinite")) {
            PotionEffectType effectType = PotionUtil.getPotionEffectType(itemInHand);
            if (effectType == null) {
                player.sendMessage(CC.translate("&cA poção não possui efeitos para modificar."));
                return;
            }

            PotionEffect newEffect = new PotionEffect(effectType, Integer.MAX_VALUE, PotionUtil.getPotionEffectAmplifier(itemInHand));
            potionMeta.clearCustomEffects();
            potionMeta.addCustomEffect(newEffect, true);
            itemInHand.setItemMeta(potionMeta);

            player.sendMessage(CC.translate("&aDuração da poção alterada para &binfinita&a."));
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[0]));
            return;
        }

        if (duration < 1) {
            player.sendMessage(CC.translate("&cDuração inválida."));
            return;
        }

        PotionEffectType effectType = PotionUtil.getPotionEffectType(itemInHand);
        if (effectType == null) {
            player.sendMessage(CC.translate("&cA poção não possui efeitos para modificar."));
            return;
        }

        PotionEffect newEffect = new PotionEffect(effectType, duration * 20, PotionUtil.getPotionEffectAmplifier(itemInHand));
        potionMeta.clearCustomEffects();
        potionMeta.addCustomEffect(newEffect, true);
        itemInHand.setItemMeta(potionMeta);

        String seconds = duration == 1 ? "segundo" : "segundos";
        player.sendMessage(CC.translate("&aDuração da poção alterada para &b" + duration + " &a" + seconds + "."));
    }
}
