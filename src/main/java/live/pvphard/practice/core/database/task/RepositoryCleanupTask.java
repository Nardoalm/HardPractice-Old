package live.pvphard.practice.core.database.task;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.duel.DuelRequest;
import live.pvphard.practice.feature.duel.DuelRequestService;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import live.pvphard.practice.feature.party.PartyRequest;
import live.pvphard.practice.feature.party.PartyService;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class RepositoryCleanupTask extends BukkitRunnable {
    protected final HardPractice plugin;

    /**
     * Constructor for the RepositoryCleanupTask class.
     *
     * @param plugin the Hard bootstrap instance
     */
    public RepositoryCleanupTask(HardPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        CombatService combatService = HardPractice.getInstance().getService(CombatService.class);
        if (!combatService.getCombatMap().isEmpty()) {
            combatService.getCombatMap().forEach((uuid, combat) -> {
                Player player = this.plugin.getServer().getPlayer(uuid);
                // Player may be null if they disconnected while still in the combat map.
                // isExpired requires a non-null Player, so we skip offline UUIDs here;
                // the map will be cleaned up naturally on their next login or server stop.
                if (player == null) return;
                if (combatService.isExpired(player)) {
                    combatService.removeLastAttacker(player, false);
                }
            });
        }

        DuelRequestService duelRequestService = HardPractice.getInstance().getService(DuelRequestService.class);
        if (!duelRequestService.getDuelRequests().isEmpty()) {
            List<DuelRequest> expiredRequests = new ArrayList<>();
            synchronized (duelRequestService.getDuelRequests()) {
                duelRequestService.getDuelRequests().removeIf(duelRequest -> {
                    if (duelRequest.hasExpired()) {
                        expiredRequests.add(duelRequest);
                        return true;
                    }
                    return false;
                });
            }
            this.notifyDuelRequestIndividuals(expiredRequests);
        }

        SnapshotService snapshotRepository = HardPractice.getInstance().getService(SnapshotService.class);
        snapshotRepository.getSnapshots().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getCreatedAt() >= 60_000);

        PartyService partyService = HardPractice.getInstance().getService(PartyService.class);
        if (!partyService.getPartyRequests().isEmpty()) {
            List<PartyRequest> expiredRequests = new ArrayList<>();
            synchronized (partyService.getPartyRequests()) {
                partyService.getPartyRequests().removeIf(request -> {
                    if (request.hasExpired()) {
                        expiredRequests.add(request);
                        return true;
                    }
                    return false;
                });
            }
            this.notifyPartyRequestIndividuals(expiredRequests);
        }
    }

    /**
     * Notify the sender and target that the duel request has expired.
     *
     * @param expiredRequests the expired requests
     */
    private void notifyDuelRequestIndividuals(List<DuelRequest> expiredRequests) {
        expiredRequests.forEach(duelRequest -> {
            if (duelRequest.getSender() == null || duelRequest.getTarget() == null) {
                return;
            }

            LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

            if (localeService.getBoolean(GameMessagesLocaleImpl.DUEL_REQUEST_EXPIRED_ENABLED_BOOLEAN)) {
                List<String> senderMessage = localeService.getStringList(GameMessagesLocaleImpl.ERROR_DUEL_REQUESTS_EXPIRED);
                for (String line : senderMessage) {
                    duelRequest.getSender().sendMessage(CC.translate(line
                            .replace("{target}", duelRequest.getTarget().getName()))
                    );
                }
            }

            if (localeService.getBoolean(GameMessagesLocaleImpl.DUEL_REQUEST_EXPIRED_TARGET_ENABLED_BOOLEAN)) {
                List<String> targetMessage = localeService.getStringList(GameMessagesLocaleImpl.DUEL_REQUEST_EXPIRED_TARGET);
                for (String line : targetMessage) {
                    duelRequest.getTarget().sendMessage(CC.translate(line
                            .replace("{sender}", duelRequest.getSender().getName()))
                    );
                }
            }
        });
    }

    /**
     * Notifies the individuals that their party request has expired.
     *
     * @param partyRequests The party requests that have expired.
     */
    private void notifyPartyRequestIndividuals(List<PartyRequest> partyRequests) {
        partyRequests.forEach(partyRequest -> {
            if (partyRequest.getSender() == null || partyRequest.getTarget() == null) {
                return;
            }

            LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

            if (localeService.getBoolean(GameMessagesLocaleImpl.PARTY_REQUEST_EXPIRED_ENABLED_BOOLEAN)) {
                List<String> senderMessage = localeService.getStringList(GameMessagesLocaleImpl.PARTY_REQUEST_EXPIRED);
                for (String line : senderMessage) {
                    partyRequest.getSender().sendMessage(CC.translate(line
                            .replace("{target}", partyRequest.getTarget().getName()))
                    );
                }
            }

            if (localeService.getBoolean(GameMessagesLocaleImpl.PARTY_REQUEST_EXPIRED_TARGET_ENABLED_BOOLEAN)) {
                List<String> targetMessage = localeService.getStringList(GameMessagesLocaleImpl.PARTY_REQUEST_EXPIRED_TARGET);
                for (String line : targetMessage) {
                    partyRequest.getTarget().sendMessage(CC.translate(line
                            .replace("{sender}", partyRequest.getSender().getName()))
                    );
                }
            }
        });
    }
}