package live.pvphard.practice.feature.tip.command;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.tip.Tip;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 25/04/2025
 */
public class TipCommand extends BaseCommand {
    private final Tip tip;

    public TipCommand() {
        this.tip = new Tip();
    }

    @CommandData(
            name = "tip",
            aliases = {"tips"},
            usage = "tip",
            description = "Obter a random tip.",
            cooldown = 5
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.sendMessage(CC.translate(this.tip.getRandomTip()));
    }
}