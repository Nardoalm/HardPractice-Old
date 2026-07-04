package live.pvphard.practice.core.profile.command.admin;

import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.time.DateFormat;
import live.pvphard.practice.common.time.DateFormatter;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.data.types.ProfilePlayTimeData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @date 24/05/2024 - 18:45
 */
public class PlaytimeCommand extends BaseCommand {
    @CommandData(
            name = "playtime",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "playtime <player>",
            description = "Verifica o tempo de jogo de um jogador."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (command.length() < 1) {
            command.sendUsage();
            return;
        }

        OfflinePlayer targetPlayer = PlayerUtil.getOfflinePlayerByName(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        Profile targetProfile = this.plugin.getService(ProfileService.class).getProfile(targetPlayer.getUniqueId());
        if (targetProfile == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        ProfilePlayTimeData playTimeData = targetProfile.getProfileData().getPlayTimeData();

        long totalPlayTime = playTimeData.getTotal();
        long totalTimeInSeconds = totalPlayTime / 1000;
        int days = (int) (totalTimeInSeconds / 86400);
        int hours = (int) (totalTimeInSeconds / 3600);
        int minutes = (int) ((totalTimeInSeconds % 3600) / 60);
        int seconds = (int) (totalTimeInSeconds % 60);

        long firstJoin = targetProfile.getFirstJoin();
        DateFormatter firstJoinFormatted = new DateFormatter(DateFormat.TIME_PLUS_DATE, firstJoin);

        List<String> messages = new ArrayList<>();
        messages.add("");
        messages.add("&b&lTempo de Jogo de " + targetPlayer.getName());
        messages.add("  &f&l● &bDias: &f" + days);
        messages.add("  &f&l● &bHoras: &f" + hours);
        messages.add("  &f&l● &bMinutos: &f" + minutes);
        messages.add("  &f&l● &bSegundos: &f" + seconds);
        messages.add("");
        messages.add("&fA primeira entrada foi em &b" + firstJoinFormatted.setFancy(ChatColor.AQUA, ChatColor.WHITE) + "&f.");
        messages.add("");

        if (targetProfile.isOnline()) {
            messages.add(" &c&lAviso: &7" + targetPlayer.getName() + " está online agora.");
            messages.add(" &7O tempo de jogo será atualizado quando ele sair.");
            messages.add("");
        }

        messages.forEach(message -> sender.sendMessage(CC.translate(message)));
    }
}
