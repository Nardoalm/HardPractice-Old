package live.pvphard.practice.feature.party.command.impl.donator;

import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.feature.party.PartyState;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Emmy
 * @project Hard
 * @date 17/11/2024 - 11:16
 */
public class PartyAnnounceCommand extends BaseCommand {
    @CommandData(
            name = "party.announce",
            aliases = {"p.announce"},
            permission = "kaos.donator.party.announce",
            usage = "party announce",
            description = "Anuncia sua party para o servidor."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        LocaleService localeService = this.plugin.getService(LocaleService.class);

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        if (!profile.getState().equals(ProfileState.LOBBY)) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
            return;
        }

        if (profile.getParty().getState() != PartyState.PUBLIC) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ERROR_YOU_PARTY_NOT_PUBLIC));
            return;
        }

        CooldownService cooldownService = this.plugin.getService(CooldownService.class);
        Optional<Cooldown> optionalCooldown = Optional.ofNullable(cooldownService.getCooldown(player.getUniqueId(), CooldownType.PARTY_ANNOUNCE_COOLDOWN));
        if (optionalCooldown.isPresent() && optionalCooldown.get().isActive()) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COOLDOWN_PARTY_ANNOUNCE_MUST_WAIT).replace("{time}", String.valueOf(optionalCooldown.get().remainingTimeInMinutes())));
            return;
        }

        Cooldown cooldown = optionalCooldown.orElseGet(() -> {
            Cooldown newCooldown = new Cooldown(CooldownType.PARTY_ANNOUNCE_COOLDOWN, () -> {
            });
            cooldownService.addCooldown(player.getUniqueId(), CooldownType.PARTY_ANNOUNCE_COOLDOWN, newCooldown);
            return newCooldown;
        });

        cooldown.resetCooldown();

        this.plugin.getService(PartyService.class).announceParty(profile.getParty());
    }
}