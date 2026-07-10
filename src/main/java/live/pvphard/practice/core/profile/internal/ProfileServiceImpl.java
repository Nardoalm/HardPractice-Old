package live.pvphard.practice.core.profile.internal;

import com.mongodb.client.MongoCollection;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.database.MongoService;
import live.pvphard.practice.core.database.model.DatabaseProfile;
import live.pvphard.practice.core.database.model.internal.MongoProfileImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.data.ProfileData;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.layout.data.LayoutData;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Service(provides = ProfileService.class, priority = 180)
public class ProfileServiceImpl implements ProfileService {
    private final Map<UUID, Profile> profiles = new HashMap<>();
    private final MongoService mongoService;

    private MongoCollection<Document> collection;
    private DatabaseProfile databaseProfile;

    public ProfileServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void initialize(HardContext context) {
        this.collection = mongoService.getMongoDatabase().getCollection("profiles");
        this.databaseProfile = new MongoProfileImpl();
    }

    @Override
    public void shutdown(HardContext context) {
        Logger.info("Saving all loaded player profiles...");
        this.profiles.values().stream()
                .filter(Objects::nonNull)
                .filter(profile -> profile.getUuid() != null && profile.getName() != null && !profile.getName().trim().isEmpty())
                .forEach(Profile::save);
        Logger.info("Profile saving complete.");
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return this.profiles.computeIfAbsent(uuid, k -> {
            String name = HardPractice.getInstance().getServer().getOfflinePlayer(k).getName();
            if (name == null || name.trim().isEmpty()) {
                name = "Unknown";
            }
            Profile profile = new Profile(k, name);
            profile.load();
            return profile;
        });
    }

    @Override
    public void removeProfile(UUID uuid) {
        this.profiles.remove(uuid);
    }

    @Override
    public void resetStats(Player player, UUID target) {
        Profile profile = this.getProfile(target);
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        this.databaseProfile.archiveProfile(profile);

        profile.setProfileData(new ProfileData());
        profile.save();

        Arrays.asList(
                "",
                "&c&lSTATISTICS RESET",
                "&cThe statistics for " + targetPlayer.getName() + " have been reset.",
                "&7Abusing this feature will lead to severe punishment.",
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line)));

        if (targetPlayer.isOnline() && targetPlayer.getPlayer() != null) {
            Arrays.asList(
                    "",
                    "&c&lSTATISTICS RESET",
                    "&cYour statistics have been wiped due to suspicious activity.",
                    "&7If you believe this was an error, please open a support ticket.",
                    ""
            ).forEach(line -> targetPlayer.getPlayer().sendMessage(CC.translate(line)));
        }
    }

    @Override
    public void resetLayoutForKit(Kit kit) {
        if (kit == null || kit.getName() == null) {
            return;
        }

        this.profiles.values().forEach(profile -> {
            if (profile == null) {
                return;
            }

            if (profile.getName() == null || profile.getName().trim().isEmpty() || profile.getName().equals("Unknown")) {
                if (profile.getUuid() != null) {
                    String resolvedName = Bukkit.getOfflinePlayer(profile.getUuid()).getName();
                    if (resolvedName != null && !resolvedName.trim().isEmpty()) {
                        profile.setName(resolvedName);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }

            if (profile.getProfileData() == null
                    || profile.getProfileData().getLayoutData() == null
                    || profile.getProfileData().getLayoutData().getLayouts() == null) {
                return;
            }

            List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(kit.getName());
            if (layouts == null || layouts.isEmpty()) {
                profile.getProfileData().getLayoutData().addLayout(
                        kit.getName(),
                        "Layout1",
                        "Modelo 1",
                        InventoryUtil.cloneItemStackArray(kit.getItems())
                );
            } else {
                layouts.stream()
                        .filter(Objects::nonNull)
                        .forEach(layout -> layout.setItems(InventoryUtil.cloneItemStackArray(kit.getItems())));
                profile.getProfileData().getLayoutData().getLayouts().put(kit.getName(), layouts);
            }

            profile.save();
        });

        Bukkit.broadcastMessage(CC.translate("&c&lLAYOUT RESET: &cThe layout for the kit " + kit.getName() + " has been reset for all players."));
    }
}