package us.alleypvp.practice.feature.command.impl.main;

import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.common.text.ClickableUtil;
import us.alleypvp.practice.library.command.BaseCommand;
import us.alleypvp.practice.library.command.CommandArgs;
import us.alleypvp.practice.library.command.annotation.CommandData;
import us.alleypvp.practice.library.command.annotation.CompleterData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Emmy
 * @project Alley
 * @date 19/04/2024 - 17:39
 */
public class KaosCommand extends BaseCommand {
    @CompleterData(name = "kaos")
    public List<String> kaosCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();
        if (command.getArgs().length == 1 && command.getPlayer().hasPermission(this.getAdminPermission())) {
            completion.addAll(Arrays.asList(
                    "reload", "debug", "core"
            ));
        }

        return completion;
    }

    @CommandData(
            name = "alley",
            aliases = {"alleypractice", "practice", "prac"},
            inGameOnly = false,
            usage = "alley",
            description = "Exibe informações sobre o plugin."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        Arrays.asList(
                "",
                " &b&lAlleyPractice",
                "      &b&l│ &fLicença: &bCC BY-NC-SA 4.0",
                "      &b&l│ &fVersão: &b" + this.plugin.getDescription().getVersion(),
                ""
        ).forEach(line -> sender.sendMessage(CC.translate(line)));

        if (sender instanceof Player) {
            TextComponent clickable = this.createLinkComponent();
            command.getPlayer().spigot().sendMessage(clickable);
            sender.sendMessage("");
        }
    }

    private @NotNull TextComponent createLinkComponent() {
        TextComponent websiteComponent = ClickableUtil.createLinkComponent("&b&l[WEBSITE]", "https://store.pvphard.live", "&aClique para abrir o website.");
        String spacing = "  ";

        TextComponent clickable = new TextComponent("     ");
        clickable.addExtra(websiteComponent);
        clickable.addExtra(spacing);
        return clickable;
    }
}
