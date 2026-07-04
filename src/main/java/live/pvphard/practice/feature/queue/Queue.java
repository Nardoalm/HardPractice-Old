package live.pvphard.practice.feature.queue;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.MatchService;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Getter
@Setter
public class Queue {
    private final Kit kit;
    private final boolean ranked;
    private final boolean duos;
    private final LinkedList<QueueProfile> profiles = new LinkedList<>();
    private final long maxQueueTime = 300000L; // 5 minutes

    /**
     * Constructor for the Queue class.
     *
     * @param kit The kit associated with the queue.
     */
    public Queue(Kit kit, boolean ranked, boolean duos) {
        this.kit = kit;
        this.ranked = ranked;
        this.duos = duos;
    }

    /**
     * Gets the amount of people playing that queue.
     *
     * @return The amount of people playing that queue.
     */
    public int getQueueFightCount() {
        MatchService matchService = HardPractice.getInstance().getService(MatchService.class);
        return (int) matchService.getMatches().stream()
                .filter(match -> match.getQueue() != null && match.getQueue().equals(this))
                .count();
    }

    /**
     * Gets the queue type.
     *
     * @return The queue type.
     */
    public String getQueueType() {
        return (this.ranked ? "Ranked" : "Unranked") + (this.duos ? " Duos" : "");
    }

    /**
     * Adds a player to the queue.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player, int elo) {
        if (player == null || !player.isOnline()) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        PartyService partyService = HardPractice.getInstance().getService(PartyService.class);
        HotbarService hotbarService = HardPractice.getInstance().getService(HotbarService.class);

        UUID uuid = player.getUniqueId();

        Profile profile = profileService.getProfile(uuid);
        Party party = partyService.getParty(player);

        if (this.isDuos() && party != null) {
            for (UUID memberId : party.getMembers()) {
                Profile memberProfile = profileService.getProfile(memberId);
                if (memberProfile != null && memberProfile.getQueueProfile() != null) {
                    player.sendMessage(CC.translate("&cSomeone in your party is already in a queue."));
                    return;
                }
            }
        } else if (this.profiles.stream().anyMatch(queueProfile -> queueProfile.getUuid().equals(uuid))) {
            player.sendMessage(CC.translate("&cYou are already in a queue."));
            return;
        }

        if (!this.isDuos() && party != null) {
            player.sendMessage(CC.translate("&cYou cannot join a 1v1 queue while in a party."));
            return;
        }


        if (this.isDuos()) {
            if (party != null && !party.isLeader(player)) {
                player.sendMessage(CC.translate("&cOnly the party leader can join the queue."));
                return;
            }

            if (party != null && party.getMembers().size() > 2) {
                player.sendMessage(CC.translate("&cYour party size is too large for duos queues."));
                return;
            }

            if (party != null && party.getMembers().size() == 2) {
                for (UUID memberId : party.getMembers()) {
                    if (Bukkit.getPlayer(memberId) == null || !Bukkit.getPlayer(memberId).isOnline()) {
                        player.sendMessage(CC.translate("&cAll party members must be online to join the queue."));
                        return;
                    }

                    Profile memberProfile = profileService.getProfile(memberId);
                    if (memberProfile.getState() != ProfileState.LOBBY) {
                        player.sendMessage(CC.translate("&cAll party members must be in the lobby to join the queue."));
                        return;
                    }
                }
            } else {
                if (profile.getState() != ProfileState.LOBBY) {
                    player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
                    return;
                }

                if (party == null || party.getMembers().size() == 1) {
                    player.sendMessage(CC.translate("&eYou joined the duos queue solo. A random teammate will be selected."));
                }
            }
        } else {
            if (profile.getState() != ProfileState.LOBBY) {
                player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
                return;
            }
        }

        QueueProfile queueProfile = new QueueProfile(this, uuid);
        queueProfile.setElo(elo);

        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.WAITING);

        this.profiles.add(queueProfile);

        if (this.isDuos() && party != null && party.getMembers().size() > 1) {
            for (UUID memberId : party.getMembers()) {
                if (!memberId.equals(uuid)) {
                    {
                        Profile memberProfile = profileService.getProfile(memberId);
                        if (memberProfile != null) {
                            memberProfile.setQueueProfile(queueProfile);
                            memberProfile.setState(ProfileState.WAITING);
                            Player memberPlayer = Bukkit.getPlayer(memberId);
                            if (memberPlayer != null) {
                                hotbarService.applyHotbarItems(memberPlayer);
                                memberPlayer.sendMessage(CC.translate("&aYour party leader has joined the &b" + queueProfile.getQueue().getKit().getDisplayName() + " &aqueue."));
                            }
                        }
                    }
                }
            }
        }

        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        if (localeService.getBoolean(GlobalMessagesLocaleImpl.QUEUE_JOINED_BOOLEAN)) {
            List<String> joinMessage = localeService.getStringList(GlobalMessagesLocaleImpl.QUEUE_JOINED);
            for (String line : joinMessage) {
                line = line.replace("{queue-type}", queueProfile.getQueue().getQueueType());
                line = line.replace("{kit}", queueProfile.getQueue().getKit().getDisplayName());
                player.sendMessage(CC.translate(line));
            }
        }

        hotbarService.applyHotbarItems(player);
    }

    /**
     * Removes a player from the queue.
     *
     * @param queueProfile The queue profile to remove.
     */
    public void removePlayer(QueueProfile queueProfile) {
        if (queueProfile == null) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        PartyService partyService = HardPractice.getInstance().getService(PartyService.class);
        HotbarService hotbarService = HardPractice.getInstance().getService(HotbarService.class);

        UUID playerToRemoveUUID = queueProfile.getUuid();
        Profile playerToRemoveProfile = profileService.getProfile(playerToRemoveUUID);
        Player playerToRemove = Bukkit.getPlayer(playerToRemoveUUID);

        Party party = partyService.getPartyByMember(playerToRemoveUUID);
        boolean removeWholeParty = this.isDuos()
                && party != null
                && party.getLeader() != null
                && party.getLeader().getUniqueId().equals(playerToRemoveUUID);

        if (removeWholeParty) {
            for (UUID memberId : party.getMembers()) {
                Profile memberProfile = profileService.getProfile(memberId);
                if (memberProfile != null && memberProfile.getQueueProfile() != null) {
                    memberProfile.setQueueProfile(null);
                    memberProfile.setState(ProfileState.LOBBY);
                    Player memberPlayer = Bukkit.getPlayer(memberId);
                    if (memberPlayer != null) {
                        hotbarService.applyHotbarItems(memberPlayer);
                        memberPlayer.sendMessage(CC.translate("&cYour party has left the queue."));
                    }
                }
            }
            this.profiles.removeIf(profile -> profile.getUuid().equals(playerToRemoveUUID));
        } else {
            this.profiles.removeIf(profile -> profile.getUuid().equals(playerToRemoveUUID));

            if (playerToRemoveProfile != null) {
                playerToRemoveProfile.setQueueProfile(null);
                playerToRemoveProfile.setState(ProfileState.LOBBY);
            }

            if (playerToRemove != null) {
                hotbarService.applyHotbarItems(playerToRemove);
                LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

                if (localeService.getBoolean(GlobalMessagesLocaleImpl.QUEUE_LEFT_BOOLEAN)) {
                    List<String> joinMessage = localeService.getStringList(GlobalMessagesLocaleImpl.QUEUE_LEFT);
                    for (String line : joinMessage) {
                        line = line.replace("{queue-type}", queueProfile.getQueue().getQueueType());
                        line = line.replace("{kit}", queueProfile.getQueue().getKit().getDisplayName());
                        playerToRemove.sendMessage(CC.translate(line));
                    }
                }
            }
        }
    }

    /**
     * Gets the profile of a player.
     *
     * @param uuid The UUID of the player.
     * @return The profile object.
     */
    public Profile getProfile(UUID uuid) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        return profileService.getProfile(uuid);
    }

    public int getTotalPlayerCount() {
        PartyService partyService = HardPractice.getInstance().getService(PartyService.class);

        int count = 0;
        for (QueueProfile queueProfile : this.profiles) {
            Party party = partyService.getPartyByMember(queueProfile.getUuid());
            if (party != null && party.getMembers().size() > 1) {
                count += party.getMembers().size();
            } else {
                count += 1;
            }
        }
        return count;
    }
}