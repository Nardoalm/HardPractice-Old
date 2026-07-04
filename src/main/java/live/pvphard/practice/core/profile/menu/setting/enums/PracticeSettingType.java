package live.pvphard.practice.core.profile.menu.setting.enums;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.LoreHelper;
import live.pvphard.practice.core.profile.data.types.ProfileSettingData;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum PracticeSettingType {
    PARTY_MESSAGES(10, "&b&lParty Messages", Material.FEATHER,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle visibility of party chat messages.",
                    "",
                    LoreHelper.displayEnabled(settings.isPartyMessagesEnabled()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),

    PARTY_INVITES(11, "&b&lParty Invites", Material.NAME_TAG,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle receiving party invitations.",
                    "",
                    LoreHelper.displayEnabled(settings.isPartyInvitesEnabled()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),

    SIDEBAR_VISIBILITY(12, "&b&lSidebar Visibility", Material.CARPET, 5,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle the scoreboard display.",
                    "",
                    LoreHelper.displayShown(settings.isScoreboardEnabled()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),

    PING_RANGE(19, "&b&lPing Range", Material.STICK,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Limit matchmaking pairs by ping difference.",
                    "",
                    "&f&l│ &bCurrent: &f" + settings.getPingRangeDisplay(),
                    "",
                    "&aClick to change.",
                    CC.MENU_BAR
            )
    ),

    WORLD_TIME(13, "&b&lWorld Time", Material.WATCH, settings -> Arrays.asList(
            CC.MENU_BAR,
            "&7Change your personal world time.",
            "",
            formatTime("Default", settings.isDefaultTime(), "&a&l"),
            formatTime("Day", settings.isDayTime(), "&e&l"),
            formatTime("Sunset", settings.isSunsetTime(), "&b&l"),
            formatTime("Night", settings.isNightTime(), "&4&l"),
            "",
            "&aClick to toggle.",
            CC.MENU_BAR
    )),

    SCOREBOARD_LINES(14, "&b&lScoreboard Lines", Material.STRING,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle scoreboard line separators.",
                    "",
                    LoreHelper.displayShown(settings.isShowScoreboardLines()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),

    DUEL_REQUESTS(21, "&b&lDuel Requests", Material.DIAMOND_SWORD,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle receiving duel requests.",
                    "",
                    LoreHelper.displayEnabled(settings.isReceiveDuelRequestsEnabled()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),
    SERVER_TITLES(20, "&b&lServer Titles", Material.PAPER,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Toggle screen titles sent by the server.",
                    "",
                    LoreHelper.displayShown(settings.isServerTitles()),
                    "",
                    "&aClick to toggle.",
                    CC.MENU_BAR
            )
    ),

    MATCH_SETTINGS(16, "&b&lMatch Settings", Material.BOOK,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Adjust your custom match settings.",
                    "",
                    "&aClick to view.",
                    CC.MENU_BAR
            )
    ),

    COSMETICS(22, "&b&lCosmetics", Material.NETHER_STAR,
            settings -> Arrays.asList(
                    CC.MENU_BAR,
                    "&7Customize your cosmetic rewards.",
                    "",
                    "&aClick to view.",
                    CC.MENU_BAR
            )
    ),

    ;

    public final int slot;
    public final String displayName;
    public final Material material;
    public final int durability;
    public final Function<ProfileSettingData, List<String>> loreProvider;

    PracticeSettingType(int slot, String displayName, Material material, Function<ProfileSettingData, List<String>> loreProvider) {
        this(slot, displayName, material, 0, loreProvider);
    }

    PracticeSettingType(int slot, String displayName, Material material, int durability, Function<ProfileSettingData, List<String>> loreProvider) {
        this.slot = slot;
        this.displayName = displayName;
        this.material = material;
        this.durability = durability;
        this.loreProvider = loreProvider;
    }

    private static String formatTime(String label, boolean active, String activeColor) {
        return " &f● " + (active ? activeColor : "&7") + label;
    }
}