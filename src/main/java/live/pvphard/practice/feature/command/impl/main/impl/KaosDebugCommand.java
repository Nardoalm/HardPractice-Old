package live.pvphard.practice.feature.command.impl.main.impl;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.setting.KitSettingService;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.emoji.EmojiService;
import live.pvphard.practice.feature.duel.DuelRequestService;
import live.pvphard.practice.feature.match.MatchService;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Emmy
 * @project Hard
 * @date 30/05/2024 - 12:15
 */
public class KaosDebugCommand extends BaseCommand {

    @CommandData(
            name = "kaos.debug",
            isAdminOnly = true,
            usage = "kaos debug <memory/instance/profile/profileData>",
            description = "Exibe informações de depuração."
    )

    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        switch (args[0]) {
            case "memory":
                this.sendMemoryInfo(player);
                break;
            case "instance":
                this.sendInstanceInfo(player);
                break;
            case "profile":
                this.sendProfileInfo(profile, player);
                break;
            case "profiledata":
                this.sendProfileData(profile, player);
                break;
            default:
                command.sendUsage();
                break;
        }
    }

    /**
     * Sends memory information to the player.
     *
     * @param player The player to send the information to.
     */
    private void sendMemoryInfo(Player player) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;

        Arrays.asList(
                "",
                "     &b&lKaos &7│ &fInformacoes de Memoria",
                "      &b&l│ &fMemoria Maxima: &b" + this.formatNumber((int) (maxMemory / 1024 / 1024)) + "MB",
                "      &b&l│ &fMemoria Alocada: &b" + this.formatNumber((int) (allocatedMemory / 1024 / 1024)) + "MB",
                "      &b&l│ &fMemoria Livre: &b" + this.formatNumber((int) (freeMemory / 1024 / 1024)) + "MB",
                "      &b&l│ &fMemoria Usada: &b" + this.formatNumber((int) (usedMemory / 1024 / 1024)) + "MB",
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line)));
    }

    /**
     * Sends instance information to the player.
     *
     * @param player The player to send the information to.
     */
    private void sendInstanceInfo(Player player) {
        Arrays.asList(
                "",
                "     &b&lKaos &7│ &fInformacoes da Instancia",
                "      &b&l│ &fPerfis: &b" + this.formatNumber(this.plugin.getService(ProfileService.class).getProfiles().size()),
                "      &b&l│ &fPartidas: &b" + this.formatNumber(this.plugin.getService(MatchService.class).getMatches().size()),
                "      &b&l│ &fFilas: &b" + this.formatNumber(this.plugin.getService(QueueService.class).getQueues().size()),
                "      &b&l│ &fPerfis em fila: &b" + this.formatNumber(Arrays.stream(this.plugin.getService(QueueService.class).getQueues().stream().mapToInt(queue -> queue.getProfiles().size()).toArray()).sum()),
                "      &b&l│ &fCooldowns: &b" + this.formatNumber(this.plugin.getService(CooldownService.class).getCooldowns().size()),
                "      &b&l│ &fCooldowns ativos: &b" + this.formatNumber((int) this.plugin.getService(CooldownService.class).getCooldowns().stream().filter(cooldown -> cooldown.getC().isActive()).count()),
                "      &b&l│ &fCombates: &b" + this.formatNumber(this.plugin.getService(CombatService.class).getCombatMap().size()),
                "      &b&l│ &fKits: &b" + this.formatNumber(this.plugin.getService(KitService.class).getKits().size()),
                "      &b&l│ &fConfiguracoes de kit: &b" + this.formatNumber(this.plugin.getService(KitSettingService.class).getSettings().size()),
                "      &b&l│ &fParties: &b" + this.formatNumber(this.plugin.getService(PartyService.class).getParties().size()),
                "      &b&l│ &fArenas: &b" + this.formatNumber(this.plugin.getService(ArenaService.class).getArenas().size()),
                "      &b&l│ &fSnapshots: &b" + this.formatNumber(this.plugin.getService(SnapshotService.class).getSnapshots().size()),
                "      &b&l│ &fPedidos de duelo: &b" + this.formatNumber(this.plugin.getService(DuelRequestService.class).getDuelRequests().size()),
                "      &b&l│ &fEmojis: &b" + this.formatNumber(this.plugin.getService(EmojiService.class).getEmojis().size()),
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line)));
    }

    /**
     * Sends profile information to the player.
     *
     * @param profile The profile to send the information for.
     * @param player  The player to send the information to.
     */
    private void sendProfileInfo(Profile profile, Player player) {
        String banned = profile.getProfileData().isRankedBanned() ? "&c&lBANIDO" : "&a&lNAO BANIDO";
        Arrays.asList(
                "",
                "     &b&lPerfil &7│ &f" + profile.getName(),
                "      &b&l│ &fUUID: &b" + profile.getUuid(),
                "      &b&l│ &fElo: &b" + this.formatNumber(profile.getProfileData().getElo()),
                "      &b&l│ &fCoins: &b" + this.formatNumber(profile.getProfileData().getCoins()),
                "      &b&l│ &fEstado: &b" + profile.getState() + " &7(" + profile.getState().getDescription() + ")",
                "      &b&l│ &fFila atual: &b" + (profile.getQueueProfile() != null ? profile.getQueueProfile().getQueue().getKit().getName() : "&c&lNULO"),
                "      &b&l│ &fRanked: &b" + banned,
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line).replace("O jogador", profile.getName())));
    }

    /**
     * Sends profile data to the player.
     *
     * @param profile The profile to send the data for.
     * @param player  The player to send the data to.
     */
    private void sendProfileData(Profile profile, Player player) {
        Arrays.asList(
                "",
                "     &b&lDados do Perfil &7│ &f" + profile.getName(),
                "      &b&l│ &fVitorias unranked: &b" + this.formatNumber(profile.getProfileData().getUnrankedWins()),
                "      &b&l│ &fDerrotas unranked: &b" + this.formatNumber(profile.getProfileData().getUnrankedLosses()),
                "      &b&l│ &fVitorias ranked: &b" + this.formatNumber(profile.getProfileData().getRankedWins()),
                "      &b&l│ &fDerrotas ranked: &b" + this.formatNumber(profile.getProfileData().getRankedLosses()),
                "      &b&l│ &fAbates FFA: &b" + this.formatNumber(profile.getProfileData().getTotalFFAKills()),
                "      &b&l│ &fMortes FFA: &b" + this.formatNumber(profile.getProfileData().getTotalFFADeaths()),
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line)));
    }

    /**
     * Formats a number with commas.
     *
     * @param number The number to format
     * @return The formatted number
     */
    private String formatNumber(int number) {
        return NumberFormat.getInstance(Locale.US).format(number);
    }
}
