package live.pvphard.practice.core.locale.internal.impl.message;

import live.pvphard.practice.common.text.Symbol;
import live.pvphard.practice.core.locale.LocaleEntry;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;

/**
 * @since 09/09/2025
 */
@Getter
public enum GlobalMessagesLocaleImpl implements LocaleEntry {
    ARENA_NOT_FOUND("messages/global-messages.yml", "arena.error.not-found", "&cThere is no arena named &b{arena-name}&c!"),
    ARENA_ALREADY_EXISTS("messages/global-messages.yml", "arena.error.already-exists", "&cAn arena with that name already exists!"),
    ARENA_NO_SELECTION("messages/global-messages.yml", "arena.error.no-selection", "&cYou have not selected the arena boundaries! Use &b/arena wand&c!"),
    ARENA_KIT_ALREADY_ADDED("messages/global-messages.yml", "arena.error.kit-already-added-to-arena", "&cThe kit &b{kit-name} &calready has been added to the arena &b{arena-name}&c!"),
    ARENA_ARENA_DOES_NOT_HAVE_KIT("messages/global-messages.yml", "arena.error.arena-does-not-have-kit", "&cThe arena &b{arena-name} &cdoes not have the kit &b{kit-name}&c!"),
    ARENA_CAN_NOT_SET_CUBOID_FFA("messages/global-messages.yml", "arena.error.cannot-set-cuboid-ffa", "&cYou cannot set cuboids for FFA arenas! Use &4/arena setsafezone pos1/pos2&c."),
    ARENA_MUST_BE_STANDALONE("messages/global-messages.yml", "arena.error.must-be-standalone", "&cThe arena &b{arena-name} &cneeds to be standalone to do this!"),
    ARENA_INVALID_PORTAL("messages/global-messages.yml", "arena.error.invalid-portal", "&cInvalid portal. Use 'red' or 'blue'."),
    ARENA_HEIGHT_LIMIT_OUT_OF_BOUNDS("messages/global-messages.yml", "arena.error.height-limit-out-of-bounds", "&cThe height limit must be between 0 and 256!"),
    ARENA_VOID_LEVEL_OUT_OF_BOUNDS("messages/global-messages.yml", "arena.error.void-level-out-of-bounds", "&cThe void level must be between 0 and 256!"),
    ARENA_FFA_ARENAS_NO_SPAWNS("messages/global-messages.yml", "arena.error.ffa-arenas-no-spawns", "&cFFA Arenas do not need spawn positions. Use &4/ffa setspawn&c."),
    ARENA_CANNOT_TOGGLE_FFA("messages/global-messages.yml", "arena.error.cannot-toggle-ffa", "&cYou cannot toggle FFA arenas!"),
    ARENA_CANNOT_ADD_KITS_TO_FFA("messages/global-messages.yml", "arena.error.cannot-add-kits-to-ffa", "&cYou cannot add kits to FFA arenas!"),
    ARENA_SPAWN_NOT_SET("messages/global-messages.yml", "arena.error.spawn-not-set", "&cYou need to set both spawn positions! (/arena setspawn)"),
    ARENA_CENTER_NOT_SET("messages/global-messages.yml", "arena.error.center-not-set", "&cYou need to set the center! (/arena setcenter)"),
    ARENA_MUST_ADD_KIT("messages/global-messages.yml", "arena.error.must-add-kit", "&cYou need to add at least one kit! (/arena addkit)"),
    ARENA_MUST_ADD_EVENT("messages/global-messages.yml", "arena.error.must-add-event", "&cYou need to add at least one event! (/arena eventadd)"),
    ARENA_ASSIGNED_KIT_NULL("messages/global-messages.yml", "arena.error.assigned-kit-null", "&cThe kit &b{kit-name} &cis assigned to this arena, but it does not exist!"),
    ARENA_STANDALONE_PORTALS_NOT_SET("messages/global-messages.yml", "arena.error.standalone-portals-not-set", "&cYou need to set both team portals! (/arena setportal)"),
    ARENA_IS_NOT_FFA("messages/global-messages.yml", "arena.error.is-not-ffa", "&cThis is not an FFA arena!"),
    ARENA_INVALID_SPAWN_TYPE("messages/global-messages.yml", "arena.error.invalid-spawn-type", "&cInvalid spawn type! Valid types: blue, red, ffa"),
    ARENA_EVENT_ALREADY_ADDED("messages/global-messages.yml", "arena.error.event-already-added", "&cThe event &b{event-name} &calready has been added to the arena &b{arena-name}&c!"),
    ARENA_EVENT_DOES_NOT_HAVE("messages/global-messages.yml", "arena.error.arena-does-not-have-event", "&cThe arena &b{arena-name} &cdoes not have the event &b{event-name}&c!"),
    ARENA_MUST_BE_EVENT("messages/global-messages.yml", "arena.error.must-be-event", "&cThe arena &b{arena-name} &cneeds to be an EVENT type to do this!"),

    ARENA_SELECTION_TOOL_ADDED("messages/global-messages.yml", "arena.command.selection-tool.added", "&aSelection tool successfully added to your inventory!"),
    ARENA_SELECTION_TOOL_REMOVED("messages/global-messages.yml", "arena.command.selection-tool.removed", "&cSelection tool successfully removed from your inventory!"),
    ARENA_SELECTED_BOUNDARY("messages/global-messages.yml", "arena.command.selected-boundary", "&aBoundary &b{boundary-type} &aset to &b{x}, &b{y}, &b{z}&a!"),
    ARENA_DISPLAY_NAME_SET("messages/global-messages.yml", "arena.command.displayname-set", "&aSet the display name of the arena &b{arena-name} &ato &r{display-name}&a!"),
    ARENA_CENTER_SET("messages/global-messages.yml", "arena.command.center-set", "&aSet the center of the arena &b{arena-name}&a!"),
    ARENA_CUBOID_SET("messages/global-messages.yml", "arena.command.cuboid-set", "&aSet the area of the arena &b{arena-name}&a!"),
    ARENA_HEIGHT_LIMIT_SET("messages/global-messages.yml", "arena.command.height-limit-set", "&aSet the height limit of the arena &b{arena-name} &ato &b{height-limit}&a!"),
    ARENA_PORTAL_SET("messages/global-messages.yml", "arena.command.portal-set", "&aSet the portal &b{portal} &afor the arena &b{arena-name}&a!"),
    ARENA_VOID_LEVEL_SET("messages/global-messages.yml", "arena.command.void-level-set", "&aSet the void level of the arena &b{arena-name} &ato &b{void-level}&a!"),
    ARENA_TOGGLED("messages/global-messages.yml", "arena.command.toggled", "&aArena &b{arena-name} &atoggled to &b{status}&a!"),
    ARENA_SPAWN_SET("messages/global-messages.yml", "arena.command.spawn-set", "&aSet the spawn &b{position} &ffor the arena &b{arena-name}&a!"),
    ARENA_FFA_SPAWN_SET("messages/global-messages.yml", "arena.command.ffa-spawn-set", "&aSet the FFA spawn for the arena &b{arena-name}&a!"),
    ARENA_KIT_ADDED("messages/global-messages.yml", "arena.command.kit-added", "&aAdded the kit &b{kit-name} &ato the arena &b{arena-name}&a!"),
    ARENA_KIT_REMOVED("messages/global-messages.yml", "arena.command.kit-removed", "&cRemoved the kit &b{kit-name} &cfrom the arena &b{arena-name}&c!"),
    ARENA_EVENT_ADDED("messages/global-messages.yml", "arena.command.event-added", "&aAdded the event &b{event-name} &ato the arena &b{arena-name}&a!"),
    ARENA_EVENT_REMOVED("messages/global-messages.yml", "arena.command.event-removed", "&cRemoved the event &b{event-name} &cfrom the arena &b{arena-name}&c!"),
    ARENA_CREATED("messages/global-messages.yml", "arena.command.created", "&aCreated a new arena named &b{arena-name} &awith the type &b{arena-type}&a!"),
    ARENA_DELETED("messages/global-messages.yml", "arena.command.deleted", "&cRemoved the arena named &b{arena-name}&c!"),
    ARENA_SAVED("messages/global-messages.yml", "arena.command.saved", "&aSaved the arena &b{arena-name}&a!"),
    ARENA_SAVED_ALL("messages/global-messages.yml", "arena.command.saved-all", "&aSaved all arenas!"),

    COOLDOWN_NOT_FOUND("messages/global-messages.yml", "cooldown.error.not-found", "&cNo cooldown found for &b{player-name} &cof type &b{cooldown-type}&c."),
    COOLDOWN_RESET("messages/global-messages.yml", "cooldown.command.reset", "&aCooldown for &b{player-name} &aof type &b{cooldown-type} &ahas been reset."),

    COOLDOWN_PEARL_MUST_WAIT("messages/global-messages.yml", "cooldown.pearl.must-wait", "&cYou need to wait &b{time} &cbefore throwing another ender pearl!"),
    COOLDOWN_CAN_NOW_USE_PEARLS_AGAIN("messages/global-messages.yml", "cooldown.pearl.can-now-use-pearls-again", "&aYou can now use ender pearls again!"),
    COOLDOWN_CANNOT_USE_AT_FFA_SPAWN("messages/global-messages.yml", "cooldown.pearl.cannot-use-at-ffa-spawn", "&cYou cannot use ender pearls while in the FFA spawn!"),

    COOLDOWN_FIREBALL_MUST_WAIT("messages/global-messages.yml", "cooldown.fireball.must-wait", "&cYou need to wait &b{time} &cbefore shooting another fireball!"),
    COOLDOWN_PARTY_ANNOUNCE_MUST_WAIT("messages/global-messages.yml", "cooldown.party-announce.must-wait", "&cYou need to wait &b{time} &cbefore announcing your party again!"),
    COOLDOWN_GOLDEN_HEAD_MUST_WAIT("messages/global-messages.yml", "cooldown.golden-head.must-wait", "&cYou need to wait &b{time} &cbefore eating another golden head!"),

    COSMETIC_NOT_OWNED("messages/global-messages.yml", "cosmetics.not-owned", "&cYou do not own the cosmetic &b{cosmetic-name}&c!"),
    COSMETIC_SELECTED("messages/global-messages.yml", "cosmetics.selected", "&aSelected the cosmetic &b{cosmetic-name}&a!"),
    COSMETIC_ALREADY_SELECTED("messages/global-messages.yml", "cosmetics.already-selected", "&cYou have already selected the cosmetic &b{cosmetic-name}&c!"),

    COSMETIC_SET_FOR_PLAYER("messages/global-messages.yml", "cosmetics.command.set", "&aSet &b{cosmetic} &a{type} as the active cosmetic for &b{player}&a!"),
    COSMETICS_NONE_REGISTERED("messages/global-messages.yml", "cosmetics.error.none-registered", "&cThere are no cosmetics registered on the server!"),
    COSMETIC_TYPE_NOT_SUPPORTED("messages/global-messages.yml", "cosmetics.error.type-not-supported", "&cThe cosmetic type &b{type} &cis not supported!"),
    COSMETIC_NOT_FOUND("messages/global-messages.yml", "cosmetics.error.not-found", "&cThere is no cosmetic named &b{input}&c!"),

    COSMETIC_ALREADY_OWNED("messages/global-messages.yml", "cosmetics.error.already-owner", "&cYou already own this cosmetic."),
    COSMETIC_PURCHASE_SUCCESS("messages/global-messages.yml", "cosmetics.purchase-success", "&aYou purchased the cosmetic &b{cosmetic}&a!"),
    COSMETIC_PURCHASE_INSUFFICIENT_FUNDS("messages/global-messages.yml", "cosmetics.error.purchase-insufficient-funds", "&cYou do not have enough coins to purchase this."),

    CHAT_CHANNEL_NOT_EXIST("messages/global-messages.yml", "chat.error.channel-not-exist", "&cThe chat channel &b{channel} &cdoes not exist."),
    CHAT_CHANNEL_SET("messages/global-messages.yml", "chat.command.channel-set", "&aYour chat channel has been set to &b{channel}&a."),
    CHAT_CHANNEL_ALREADY_IN("messages/global-messages.yml", "chat.error.already-in-channel", "&cYou are already in the chat channel &b{channel}&c."),

    CHAT_CLEARED_BY_STAFF("messages/global-messages.yml", "chat.cleared-by-staff", "&c&lTHE CHAT HAS BEEN CLEARED BY STAFF!"),

    CRAFTING_TOGGLED("messages/global-messages.yml", "crafting-operations.command.toggled", "&aCrafting operations for &b{item} &aare now &b{status}&a."),
    CRAFTING_MUST_HOLD_CRAFTABLE_ITEM("messages/global-messages.yml", "crafting-operations.error.must-hold-craftable-item", "&cYou need to be holding a craftable item to manage crafting."),

    DIVISION_NOT_FOUND("messages/global-messages.yml", "division.error.not-found", "&cThere is no division named &b{division-name}&c!"),
    DIVISION_TIER_NOT_FOUND("messages/global-messages.yml", "division.error.tier-not-found", "&cThere is no tier named &b{tier-name} &cin the division &b{division-name}&c!"),
    DIVISION_ALREADY_EXISTS("messages/global-messages.yml", "division.error.already-exists", "&cThe name &b{division-name} &cis already being used by another division!"),

    DIVISION_DESCRIPTION_SET("messages/global-messages.yml", "division.command.description-set", "&aSet the description of the division &b{division-name} &ato &r{description}&a!"),
    DIVISION_DISPLAY_NAME_SET("messages/global-messages.yml", "division.command.display-name-set", "&aSet the display name of the division &b{division-name} &ato &r{display-name}&a!"),
    DIVISION_ICON_SET("messages/global-messages.yml", "division.command.icon-set", "&aSet the icon of the division &b{division-name} &ato &b{item-type}:{item-durability}&a!"),
    DIVISION_WINS_SET("messages/global-messages.yml", "division.command.wins-set", "&aSet the required wins of the tier &b{tier-name} &ain the division &b{division-name} &ato &b{required-wins}&a!"),
    DIVISION_CREATED("messages/global-messages.yml", "division.command.created", "&aCreated a new division named &b{division-name} &awith &b{required-wins} &awins!"),
    DIVISION_DELETED("messages/global-messages.yml", "division.command.deleted", "&cRemoved the division named &b{division-name}&c!"),

    EXPLOSIVE_SETTING_UPDATED("messages/global-messages.yml", "explosive.command.setting-updated", "&aSet the value of {setting-name} for explosives to &b{setting-value}&a."),

    ERROR_AMOUNT_MUST_BE_GREATER_THAN_ZERO("messages/global-messages.yml", "error-messages.amount-must-be-greater-than-zero", "&cThe amount must be greater than zero!"),

    ERROR_DUEL_REQUESTS_EXPIRED("messages/global-messages.yml", "error-messages.duel-requests.error.expired", "&cThis duel request has expired."),
    ERROR_DUEL_REQUESTS_NO_ARENA("messages/global-messages.yml", "error-messages.duel-requests.error.no-arenas", "&cThere is no arena available for this kit."),
    ERROR_DUEL_REQUESTS_CANT_DUEL_SELF("messages/global-messages.yml", "error-messages.duel-requests.error.cant-duel-self", "&cYou cannot duel yourself!"),
    ERROR_DUEL_REQUESTS_ALREADY_PENDING("messages/global-messages.yml", "error-messages.duel-requests.error.already-pending", "&cYou already have a pending duel request from this player."),
    ERROR_DUEL_REQUESTS_ALREADY_PENDING_PARTY("messages/global-messages.yml", "error-messages.duel-requests.error.already-pending-party", "&cYou already have a pending duel request from this player or their party."),
    ERROR_DUEL_REQUESTS_INVALID_FROM_PLAYER("messages/global-messages.yml", "error-messages.duel-requests.error.no-pending-request", "&cYou do not have a pending duel request from this player."),
    ERROR_DUEL_REQUESTS_REQUESTS_DISABLED_PLAYER("messages/global-messages.yml", "error-messages.duel-requests.error.player-requests-disabled", "&c{name-color}{player} has duel requests disabled."),

    ERROR_INVALID_PAGE_NUMBER("messages/global-messages.yml", "error-messages.invalid-page-number", "&c'{input}' is not a valid page number! Enter a valid number."),
    ERROR_INVALID_NUMBER("messages/global-messages.yml", "error-messages.invalid.number", "&c'{input}' is not a valid number! Enter a valid number."),
    ERROR_INVALID_PLAYER("messages/global-messages.yml", "error-messages.invalid.player", "&cThat player was not found!"),
    ERROR_INVALID_TYPE("messages/global-messages.yml", "error-messages.invalid.type", "&cInvalid {type}. Available types: &b{types}&c."),
    ERROR_INVALID_ITEM("messages/global-messages.yml", "error-messages.invalid.item", "&cInvalid item!"),
    ERROR_INVALID_BOOLEAN("messages/global-messages.yml", "error-messages.invalid-parameters", "&cInvalid parameters! Use true or false."),
    ERROR_NO_MORE_PAGES_AVAILABLE("messages/global-messages.yml", "error-messages.no-more-pages-available", "&c{input} is not a valid page number! There are only &b{max-pages} &cpages available."),

    ERROR_PLAYER_IS_BUSY("messages/global-messages.yml", "error-messages.player.is-busy", "&b{name-color}{player} &cis busy."),
    ERROR_PLAYER_NOT_PLAYING_MATCH("messages/global-messages.yml", "error-messages.player.not-playing-match", "&c{name-color}{player} &cis not in a match."),
    ERROR_PLAYER_NOT_PLAYING_FFA("messages/global-messages.yml", "error-messages.player.not-playing-ffa", "&c{name-color}{player} &cis not in an FFA match!"),
    ERROR_PLAYER_PARTY_INVITES_DISABLED("messages/global-messages.yml", "error-messages.player.party-invites-disabled", "&c{name-color}{player} &chas party invites disabled."),

    ERROR_MUST_SELECT_MUSIC("messages/global-messages.yml", "profile-messages.error.must-select-music-before-toggle", "&cYou must select at least one disc before toggling music!"),
    ERROR_YOU_MUST_LEAVE_PARTY("messages/global-messages.yml", "error-messages.you.must-leave-party", "&cYou must leave your party before doing this!"),
    ERROR_YOU_MUST_BE_IN_LOBBY("messages/global-messages.yml", "error-messages.you.must-be-in-lobby", "&cYou must be in the lobby to do this!"),
    ERROR_YOU_MUST_HOLD_ITEM("messages/global-messages.yml", "error-messages.you.must-hold-item", "&cYou must be holding an item!"),
    ERROR_YOU_NOT_PLAYING_FFA("messages/global-messages.yml", "error-messages.you.not-playing-ffa", "&cYou are not in an FFA match!"),
    ERROR_YOU_NOT_SPECTATING_FFA("messages/global-messages.yml", "error-messages.you.not-spectating-ffa", "&cYou are not spectating an FFA match!"),
    ERROR_YOU_ALREADY_SPECTATING_FFA("messages/global-messages.yml", "error-messages.you.already-spectating-ffa", "&cYou are already spectating the FFA!"),
    ERROR_YOU_NOT_PLAYING_MATCH("messages/global-messages.yml", "error-messages.you.not-playing-match", "&cYou are not in a match!"),
    ERROR_YOU_ALREADY_PLAYING_MATCH("messages/global-messages.yml", "error-messages.you.already-playing-match", "&cYou are already in a match!"),
    ERROR_YOU_NOT_SPECTATING_MATCH("messages/global-messages.yml", "error-messages.you.not-spectating-match", "&cYou are not spectating a match!"),
    ERROR_YOU_ALREADY_SPECTATING_MATCH("messages/global-messages.yml", "error-messages.you.already-spectating-match", "&cYou are already spectating a match!"),
    ERROR_YOU_NO_MATCH_HISTORY("messages/global-messages.yml", "error-messages.you.no-match-history", "&cYou do not have any match history."),
    ERROR_YOU_PARTY_NOT_PUBLIC("messages/global-messages.yml", "error-messages.you.party-not-public", "&cYour party is not public to announce. Use: &7/party open"),

    ERROR_YOU_NOT_IN_PARTY("messages/global-messages.yml", "error-messages.you..not-in-party", "&cYou are not in a party."),
    ERROR_YOU_NOT_PARTY_LEADER("messages/global-messages.yml", "error-messages.you.not-party-leader", "&cYou are not the party leader."),
    ERROR_YOU_ALREADY_IN_PARTY("messages/global-messages.yml", "error-messages.you.already-in-party", "&cYou are already in a party."),
    ERROR_YOU_ALREADY_IN_THIS_PARTY("messages/global-messages.yml", "error-messages.you.already-in-this-party", "&cYou are already in this party."),
    ERROR_YOU_PARTY_CHAT_DISABLED("messages/global-messages.yml", "error-messages.you.party-chat-disabled", "&cYou have party messages disabled. &7(To enable: /togglepartymessages)"),
    ERROR_YOU_NO_PARTY_INVITE_FROM_PLAYER("messages/global-messages.yml", "error-messages.you.no-party-invite-from-player", "&cYou do not have a party invite from &b{name-color}{player}&c."),
    ERROR_YOU_PARTY_NEED_TWO_PLAYERS("messages/global-messages.yml", "error-messages.you.party-need-two-players", "&cYou need at least two players."),
    ERROR_YOU_ARE_IN_COMBAT("messages/global-messages.yml", "error-messages.you.are-in-combat", "&cYou are in combat!"),

    ERROR_YOU_BANNED_FROM_PARTY("messages/global-messages.yml", "error-messages.you.banned-from-party", "&cYou are banned from the party of &b{name-color}{player}&c."),

    FFA_ADDED_PLAYER("messages/global-messages.yml", "ffa.added-player", "&a&lADDED! &b{name-color}{player} &7&l» &bFFA {ffa-name}"),
    FFA_KICKED_PLAYER("messages/global-messages.yml", "ffa.kicked-player", "&c&lKICKED! &b{name-color}{player} &7&l» &bFFA {ffa-name}"),
    FFA_SPAWN_ENTERED("messages/global-messages.yml", "ffa.entered-spawn", "&aYou entered the FFA spawn."),
    FFA_SPAWN_LEFT("messages/global-messages.yml", "ffa.left-spawn", "&aYou left the FFA spawn."),

    FFA_ALREADY_EXISTS("messages/global-messages.yml", "ffa.error.already-exists", "&cAn FFA match named &b{ffa-name} &calready exists!"),
    FFA_NOT_FOUND("messages/global-messages.yml", "ffa.error.not-found", "&cAn FFA match named &b{ffa-name} &cdoes not exist!"),
    FFA_DISABLED("messages/global-messages.yml", "ffa.error.disabled", "&cFFA mode is disabled for the kit &b{kit-name}&c!"),
    FFA_KIT_NOT_ELIGIBLE("messages/global-messages.yml", "ffa.error.kit-not-eligible", "&cThis kit is not eligible for FFA matches! Disable the &bBUILD/BOXING SETTINGS &cand try again."),
    FFA_CAN_ONLY_SETUP_IN_FFA_ARENA("messages/global-messages.yml", "ffa.error.can-only-setup-in-ffa-arena", "&cYou can only setup FFA matches in FFA arenas!"),
    FFA_FULL("messages/global-messages.yml", "ffa.error.ffa-full", "&cThis FFA match is full!"),
    FFA_INVALID_SPAWN_TYPE("messages/global-messages.yml", "ffa.error.invalid-spawn-type", "&cInvalid spawn type! Valid types: pos1, pos2"),

    FFA_MATCH_CREATED("messages/global-messages.yml", "ffa.command.created", "&aCreated a new FFA match for the kit &b{kit-name}&a!"),
    FFA_TOGGLED("messages/global-messages.yml", "ffa.command.toggled", "&aFFA mode has been &b{status} &afor the kit &b{kit-name}&a!"),
    FFA_ARENA_SET("messages/global-messages.yml", "ffa.command.arena-set", "&aSet the arena for the FFA match of the kit &b{kit-name} &ato &b{arena-name}&a!"),
    FFA_SAFE_ZONE_SET("messages/global-messages.yml", "ffa.command.safe-zone-set", "&aSet the safezone &b{pos} &afor the FFA match of the kit &b{kit-name}&a!"),
    FFA_MAX_PLAYERS_SET("messages/global-messages.yml", "ffa.command.max-players-set", "&aSet the maximum players for the FFA match of the kit &b{kit-name} &ato &b{max-players}&a!"),
    FFA_SPAWN_SET("messages/global-messages.yml", "ffa.command.spawn-set", "&aThe FFA spawn position has been set to &b{arena-name}&a!"),

    FFA_KITS_RELOADED("messages/global-messages.yml", "ffa.command.kits-reloaded", "&aReloaded all FFA kits!"),

    HOTBAR_NOT_FOUND("messages/global-messages.yml", "hotbar.error.not-found", "&cThe hotbar item named &e{hotbar-name} &cdoes not exist."),
    HOTBAR_NO_HOTBAR_ITEMS_CREATED("messages/global-messages.yml", "hotbar.error.no-hotbar-items-created", "&cNo hotbar items have been created yet."),

    HOTBAR_CREATED_ITEM("messages/global-messages.yml", "hotbar.command.created", "&aYou have created a new hotbar item named &e{hotbar-name}&a."),
    HOTBAR_DELETED_ITEM("messages/global-messages.yml", "hotbar.command.deleted", "&aYou have removed the hotbar item named &e{hotbar-name}&a."),

    ITEM_GIVEN("messages/global-messages.yml", "item.command.given-item", "&aYou have received &b{amount} &a{item-name}&a."),
    ITEM_NOT_CONFIGURED("messages/global-messages.yml", "item.error.item-not-configured", "&cThe item named &b{item-name} &cis not configured."),

    ITEM_ENCHANTED("messages/global-messages.yml", "item.command.enchanted-item", "&aYou have enchanted your &b{item-name} &awith &b{enchantment} &alevel &b{level}&a."),

    JOIN_MESSAGE_CHAT_ENABLED("messages/global-messages.yml", "join-message.enabled", true),

    KIT_NOT_FOUND("messages/global-messages.yml", "kit.error.not-found", "&cThere is no kit with that name!"),
    KIT_ALREADY_EXISTS("messages/global-messages.yml", "kit.error.already-exists", "&cThere is already a kit with that name!"),
    KIT_SLOT_MUST_BE_NUMBER("messages/global-messages.yml", "kit.error.slot-must-be-number", "&cThe slot must be a number!"),

    KIT_POTION_EFFECT_REMOVED("messages/global-messages.yml", "kit.command.potion-effect-removed", "&cYou have removed the potion effect &b{potion-effect} &cfrom the kit &b{kit-name}&c."),
    KIT_RAIDING_ROLE_KIT_REMOVED("messages/global-messages.yml", "kit.command.raiding-role-kit-removed", "&aRemoved the raiding role kit &b{role} &aof &b{role-kit-name}&a for the kit &b{kit-name}&a."),

    KIT_INVENTORY_GIVEN("messages/global-messages.yml", "kit.command.inventory-given", "&aRecovered the inventory of the kit &b{kit-name}&a!"),
    KIT_INVENTORY_SET("messages/global-messages.yml", "kit.command.inventory-set", "&aSet the inventory of the kit &b{kit-name}&a!"),
    KIT_FFA_SLOT_SET("messages/global-messages.yml", "kit.command.ffaslot-set", "&aSet the FFA slot of the kit &b{kit-name} &ato &b{slot}&a!"),
    KIT_DESCRIPTION_SET("messages/global-messages.yml", "kit.command.description-set", "&aSet the description of the kit &b{kit-name}&a: &r{description}"),
    KIT_DESCRIPTION_CLEARED("messages/global-messages.yml", "kit.command.description-cleared", "&aCleared the description of the kit &b{kit-name}&a!"),
    KIT_DISCLAIMER_SET("messages/global-messages.yml", "kit.command.disclaimer-set", "&aSet the disclaimer of the kit &b{kit-name}&a: &r{disclaimer}"),
    KIT_DISPLAYNAME_SET("messages/global-messages.yml", "kit.command.displayname-set", "&aSet the display name of the kit &b{kit-name}&a: &r{display-name}"),
    KIT_ICON_SET("messages/global-messages.yml", "kit.command.icon-set", "&aSet the icon of the kit &b{kit-name} &ato &b{icon}&a!"),
    KIT_CATEGORY_SET("messages/global-messages.yml", "kit.command.category-set", "&aSet the category of the kit &b{kit-name} &ato &b{category}&a!"),
    KIT_MENU_TITLE_SET("messages/global-messages.yml", "kit.command.menu-title-set", "&aSet the menu title of the kit &b{kit-name}&a: &r{title}"),
    KIT_KB_PROFILE_SET("messages/global-messages.yml", "kit.command.kb-profile-set", "&aSet the knockback profile of the kit &b{kit-name} &ato &b{kb-profile}&a!"),
    KIT_SET_EDITABLE("messages/global-messages.yml", "kit.command.set-editable", "&aSet the editable state of the kit &b{kit-name} &ato &b{editable}&a!"),
    KIT_SET_RAIDING_ROLE_KIT("messages/global-messages.yml", "kit.command.set-raiding-role-kit", "&aSet the raiding role kit &b{role} &ato &b{role-kit} &afor the kit &b{kit-name}&a."),
    KIT_CANNOT_SET_ENABLED_AS_RAIDING_ROLE_KIT("messages/global-messages.yml", "kit.error.cannot-set-enabled-as-raiding-role-kit", "&cThe kit &b{role-kit} &cis enabled. Disable it before setting it as a raiding role kit."),
    KIT_NEED_TO_DISABLE_TO_SET_RAIDING_ROLE("messages/global-messages.yml", "kit.error.need-to-disable-to-set-raiding-role-kit", "&cYou need to disable the kit &b{kit-name} &cbefore setting a raiding role kit."),
    KIT_RAIDING_ROLE_KIT_NOT_MAPPED("messages/global-messages.yml", "kit.error.raiding-role-kit-not-present", "&cThe kit &b{kit-name} &cdoes not have a raiding kit mapped for the role &b{role}&c."),

    KIT_SETTING_NOT_ENABLED("messages/global-messages.yml", "kit.error.setting-not-enabled", "&cThe setting &b{setting-name} &cis not enabled for the kit &b{kit-name}&c."),

    KIT_POTION_EFFECTS_SET("messages/global-messages.yml", "kit.command.potion-effects-set", "&aSet the potion effects of the kit &b{kit-name}&a!"),
    KIT_POTION_EFFECTS_CLEARED("messages/global-messages.yml", "kit.command.potion-effects-cleared", "&aCleared the potion effects of the kit &b{kit-name}&a!"),
    KIT_CREATED("messages/global-messages.yml", "kit.command.created", "&aCreated a new kit named &b{kit-name}&a!"),
    KIT_DELETED("messages/global-messages.yml", "kit.command.deleted", "&cRemoved the kit named &b{kit-name}&c!"),
    KIT_SETTING_SET("messages/global-messages.yml", "kit.command.setting-set", "&aSet the setting &b{setting-name} &ato &b{enabled} &afor the kit &b{kit-name}&a."),
    KIT_SAVED("messages/global-messages.yml", "kit.command.saved", "&aSaved the kit &b{kit-name}&a!"),
    KIT_SAVED_ALL("messages/global-messages.yml", "kit.command.saved-all", "&aSaved all kits!"),

    LEVEL_NOT_FOUND("messages/global-messages.yml", "level.error.not-found", "&cThere is no level named &b{level-name}&c!"),
    LEVEL_ALREADY_EXISTS("messages/global-messages.yml", "level.error.already-exists", "&cThere is already a level named &b{level-name}&c!"),
    LEVEL_MAX_ELO_MUST_BE_GREATER_THAN_MIN("messages/global-messages.yml", "level.error.max-elo-must-be-greater-than-min", "&cThe maximum Elo must be greater than the minimum Elo! (Minimum: &b{min-elo}&c)"),
    LEVEL_MINIMUM_ELO_CANNOT_BE_NEGATIVE("messages/global-messages.yml", "level.error.min-elo-cannot-be-negative", "&cThe minimum Elo cannot be negative!"),
    LEVEL_MINIMUM_ELO_MUST_BE_LESS_THAN_MAXIMUM("messages/global-messages.yml", "level.error.min-elo-must-be-less-than-maximum", "&cThe minimum Elo must be less than the maximum Elo! (Maximum: &b{max-elo}&c)"),

    LEVEL_DISPLAY_NAME_SET("messages/global-messages.yml", "level.command.display-name-set", "&aSet the display name of the level &b{level-name} &ato &r{display-name}&a!"),
    LEVEL_ICON_SET("messages/global-messages.yml", "level.command.icon-set", "&aSet the icon of the level &b{level-name} &ato &b{icon-material}&a!"),
    LEVEL_MINIMUM_ELO_SET("messages/global-messages.yml", "level.command.min-elo-set", "&aSet the minimum Elo of the level &b{level-name} &ato &b{min-elo}&a!"),
    LEVEL_MAX_ELO_SET("messages/global-messages.yml", "level.command.max-elo-set", "&aSet the maximum Elo of the level &b{level-name} &ato &b{max-elo}&a!"),
    LEVEL_CREATED("messages/global-messages.yml", "level.command.created", "&aCreated a new level named &b{level-name} &awith minimum Elo &b{min-elo} &aand maximum Elo &b{max-elo}&a!"),
    LEVEL_DELETED("messages/global-messages.yml", "level.command.deleted", "&cRemoved the level named &b{level-name}&c!"),

    MATCH_CANCELLED_FOR_PLAYER("messages/global-messages.yml", "match.cancelled", "&aYou have ended the match for &b{name-color}{player}&a."),
    MATCH_COMMAND_BLOCKED("messages/global-messages.yml", "match.error.command-blocked", "&cYou cannot use this command during a match!"),

    MUSIC_DISC_DESELECTED("messages/global-messages.yml", "music-disc.deselected", "&cYou removed &b{disc} &cfrom your music selection."),
    MUSIC_DISC_SELECTED("messages/global-messages.yml", "music-disc.selected", "&aYou added &b{disc} &ato your music selection."),
    MUSIC_DISC_NOW_PLAYING("messages/global-messages.yml", "music-disc.now-playing", Collections.singletonList("&7[&b♬&7] &fNow playing: &b{disc} &7({duration})")),

    OTHER_SUDO_ALL_PLAYERS("messages/global-messages.yml", "other.sudo.all-players", "&aYou have forced all players to say: &r{message}"),

    PARTY_DISBANDED("messages/global-messages.yml", "party.disbanded", "&b&lParty &7&l" + Symbol.ARROW_R + " &b{name-color}{player} &cdisbanded the party."),
    PARTY_YOU_JOINED("messages/global-messages.yml", "party.joined",
            Arrays.asList(
                    "",
                    "&b&lJoined Party &a" + Symbol.TICK,
                    " &7You joined the party of &b{name-color}{leader}&a.",
                    " &7Type /p for help.",
                    ""
            )
    ),
    PARTY_YOU_LEFT("messages/global-messages.yml", "party.left", "&cYou left the party!"),

    PARTY_PLAYER_JOINED("messages/global-messages.yml", "party.player-joined", "&b{name-color}{player} &ajoined the party! &7({current-size}/{max-size})"),
    PARTY_PLAYER_LEFT("messages/global-messages.yml", "party.player-left", "&b{name-color}{player} &cleft the party. &7({current-size}/{max-size})"),

    PARTY_PLAYER_KICKED("messages/global-messages.yml", "party.player-kicked", "&b{name-color}{player} &cwas kicked from the party. &7({current-size}/{max-size})"),
    PARTY_PLAYER_BANNED("messages/global-messages.yml", "party.player-banned", "&b{name-color}{player} &cwas banned from the party. &7({current-size}/{max-size})"),

    PARTY_CREATED("messages/global-messages.yml", "party.created",
            Arrays.asList(
                    "",
                    "&b&lParty Created &a" + Symbol.TICK,
                    " &7Type /p for help.",
                    ""
            )
    ),
    PARTY_LOOKUP("messages/global-messages.yml", "party.lookup", Arrays.asList(
            "",
            " &b&lParty of {leader}",
            "  &b&l│ &rLeader: &b{name-color}{leader}",
            "  &b&l│ &rMembers: &b{members}",
            "  &b&l│ &rStatus: &b{status}",
            "  &b&l│ &rPrivacy: &b{privacy}",
            ""
    )),
    PARTY_INFO_NO_MEMBERS_FORMAT("messages/global-messages.yml", "party.info.no-members-format", "&cNo Members"),
    PARTY_INFO("messages/global-messages.yml", "party.info.format", Arrays.asList(
            "",
            " &b&lParty Information",
            "  &b&l│ &rLeader: &b{name-color}{leader}",
            "  &b&l│ &rMembers &7({members-amount})&f: &b{members}",
            "  &b&l│ &rPrivacy: &b{privacy}",
            "  &b&l│ &rSize: &b{size}",
            ""
    )),

    PROFILE_TOGGLED_PARTY_INVITES("messages/global-messages.yml", "profile-messages.player-settings.party-invites", "&aYou {status} &aparty invites."),
    PROFILE_TOGGLED_PARTY_MESSAGES("messages/global-messages.yml", "profile-messages.player-settings.party-messages", "&aYou {status} &aparty messages."),
    PROFILE_TOGGLED_SCOREBOARD("messages/global-messages.yml", "profile-messages.player-settings.scoreboard", "&aYou {status} &athe scoreboard."),
    PROFILE_TOGGLED_SCOREBOARD_LINES("messages/global-messages.yml", "profile-messages.player-settings.scoreboard-lines", "&aYou {status} &athe scoreboard lines."),
    PROFILE_TOGGLED_DUEL_REQUESTS("messages/global-messages.yml", "profile-messages.player-settings.duel-requests", "&aYou {status} &areceiving duel requests."),
    PROFILE_TOGGLED_LOBBY_MUSIC("messages/global-messages.yml", "profile-messages.player-settings.lobby-music", "&aYou {status} &athe lobby music."),
    PROFILE_TOGGLED_SERVER_TITLES("messages/global-messages.yml", "profile-messages.player-settings.server-titles", "&aYou {status} &athe server titles."),
    PROFILE_WORLD_TIME_SET("messages/global-messages.yml", "profile-messages.world-time-set", "&aSet your personal world time to &b{time}&a."),
    PROFILE_WORLD_TIME_RESET("messages/global-messages.yml", "profile-messages.world-time-reset", "&aYour personal world time has been reset to server time."),

    QUEUE_TEMPORARILY_DISABLED("messages/global-messages.yml", "queue.error.temporarily-disabled", "&cQueues are temporarily disabled. Try again later."),
    QUEUE_TOGGLED("messages/global-messages.yml", "queue.command.toggled", "&aYou {status} &atemporarily the queues for all players."),
    QUEUE_RELOADED("messages/global-messages.yml", "queue.command.reloaded", "&aReloaded all queues!"),
    QUEUE_FORCED_PLAYER("messages/global-messages.yml", "queue.command.forced-player", "&aForced &b{player} &ato join the queue &b{ranked} {kit}&a!"),

    QUEUE_PROGRESSING_UNRANKED_BOOLEAN("messages/global-messages.yml", "queue.joined.progressing.unranked.enabled", true),
    QUEUE_PROGRESSING_UNRANKED("messages/global-messages.yml", "queue.joined.progressing.unranked.format", Arrays.asList(
            "",
            "&b&l{kit}",
            " &b&l│ &fPing Range: &b{ping-range}",
            "  &7&oSearching for match...",
            ""
    )),

    QUEUE_PROGRESSING_RANKED_BOOLEAN("messages/global-messages.yml", "queue.joined.progressing.ranked.enabled", true),
    QUEUE_PROGRESSING_RANKED("messages/global-messages.yml", "queue.joined.progressing.ranked.format", Arrays.asList(
            "",
            "&b&l{kit} &b&l" + Symbol.RANKED_STAR + "Ranked",
            " &b&l│ &fELO Range: &b{min-elo} &7&l" + Symbol.ARROW_R + " &b{max-elo}",
            " &b&l│ &fPing Range: &b{ping-range}",
            "  &7&oSearching for match...",
            ""
    )),

    QUEUE_PROGRESSING_RANKED_LIMIT_REACHED_BOOLEAN("messages/global-messages.yml", "queue.joined.progressing.ranked.limit-reached.enabled", true),
    QUEUE_PROGRESSING_RANKED_LIMIT_REACHED("messages/global-messages.yml", "queue.joined.progressing.ranked.limit-reached.format", Arrays.asList(
            "",
            "&b&l{kit} &b&l" + Symbol.RANKED_STAR + "Ranked",
            " &b&l│ &fPing Range: &b{ping-range}",
            "  &c&lRANGE LIMIT REACHED...",
            ""
    )),

    QUEUE_JOINED_BOOLEAN("messages/global-messages.yml", "queue.join-message.enabled", true),
    QUEUE_JOINED("messages/global-messages.yml", "queue.join-message.format", Arrays.asList(
            "",
            "&b&lJOINED QUEUE &a" + Symbol.TICK,
            " &b&l│ &rKit: &b{kit}",
            " &b&l│ &rType: &b{queue-type}",
            "  &7&oUse /leavequeue to leave.",
            ""
    )),

    QUEUE_LEFT_BOOLEAN("messages/global-messages.yml", "queue.leave-message.enabled", true),
    QUEUE_LEFT("messages/global-messages.yml", "queue.leave-message.format", Arrays.asList(
            "",
            "&b&lLEFT QUEUE &c" + Symbol.CROSS,
            " &b&l│ &rYou left the &b{queue-type} &b{kit} &rqueue.",
            ""
    )),

    RANKED_PLAYER_NOT_BANNED("messages/global-messages.yml", "ranked.error.player-not-banned", "&c{name-color}{player} &cis not banned from ranked matches!"),
    RANKED_PLAYER_ALREADY_BANNED("messages/global-messages.yml", "ranked.error.player-already-banned", "&c{name-color}{player} &cis already banned from ranked matches!"),

    RANKED_PLAYER_BAN_BROADCAST_BOOLEAN("messages/global-messages.yml", "ranked.ban-broadcast.enabled", true),
    RANKED_PLAYER_BAN_BROADCAST("messages/global-messages.yml", "ranked.ban-broadcast.format", Collections.singletonList("&c&l{name-color}{player} &7has been banned from ranked matches.")),

    RANKED_BAN_MESSAGE_NOTICE_BOOLEAN("messages/global-messages.yml", "ranked.ban-message-notice.enabled", true),
    RANKED_BAN_MESSAGE_NOTICE("messages/global-messages.yml", "ranked.ban-message-notice.format", Arrays.asList(
            "",
            "&c&lRANKED BANNED",
            " &c&l│ &fReason: &c{reason}",
            " &c&l│ &fDuration: &c{duration}",
            " &c&l│ &fBan ID: &c{ban-id}",
            ""
    )),

    RANKED_PLAYER_UNBAN_BROADCAST_BOOLEAN("messages/global-messages.yml", "ranked.unban-broadcast.enabled", true),
    RANKED_PLAYER_UNBAN_BROADCAST("messages/global-messages.yml", "ranked.unban-broadcast.format", Collections.singletonList("&a&l{name-color}{player} &7has been unbanned from ranked matches.")),

    RANKED_UNBAN_MESSAGE_NOTICE_BOOLEAN("messages/global-messages.yml", "ranked.unban-message-notice.enabled", true),
    RANKED_UNBAN_MESSAGE_NOTICE("messages/global-messages.yml", "ranked.unban-message-notice.format", Arrays.asList(
            "",
            "&a&lRANKED UNBANNED",
            " &a&l│ &fReason: &a{reason}",
            " &a&l│ &fBan ID: &a{ban-id}",
            " &a(You can now join the ranked queue again)",
            ""
    )),

    SPAWN_SET("messages/global-messages.yml", "spawn.command.set", "&aSpawn location set successfully to &bKaosPractice&a! \n &8- &7{world}: {x}, {y}, {z} (Yaw: {yaw}, Pitch: {pitch})"),
    SPAWN_TELEPORTED("messages/global-messages.yml", "spawn.command.teleported", "&bYou have been teleported to spawn!"),
    SPAWN_ITEMS_GIVEN("messages/global-messages.yml", "spawn.command.items-given", "&aSpawn items received successfully!"),

    SNAPSHOT_INVENTORY_EXPIRED("messages/global-messages.yml", "snapshot.error.inventory-expired", "&cThis inventory has expired."),

    TIPS_LIST("messages/global-messages.yml", "tips", Arrays.asList(
            "&bTip: &fUse F5 to look at your opponent one last time before it's over.",
            "&bTip: &fW-tap like your life depends on it. Because it does.",
            "&bTip: &fKeep your aim at head height... unless you like hitting feet.",
            "&bTip: &fPractice spacing. Or just stick to the enemy and pray."
    )),

    TROLL_PLAYER_DONUTED("messages/global-messages.yml", "troll.player-donuted", "&aYou applied the donut troll to &b{name-color}{player}&a!"),
    TROLL_PLAYER_FAKE_EXPLODED("messages/global-messages.yml", "troll.player-fake-exploded", "&aYou simulated an explosion for &b{name-color}{player}&a!"),
    TROLL_PLAYER_GIVEN_HEART_ATTACK("messages/global-messages.yml", "troll.player-given-heart-attack", "&aYou gave a heart attack to &b{name-color}{player}&a!"),
    TROLL_PLAYER_LAUNCHED("messages/global-messages.yml", "troll.player-launched", "&aYou launched &b{name-color}{player}&a into the sky!"),
    TROLL_PLAYER_PUSHED("messages/global-messages.yml", "troll.player-pushed", "&aYou pushed &b{name-color}{player}&a!"),
    TROLL_PLAYER_STRUCK_BY_LIGHTNING("messages/global-messages.yml", "troll.player-struck-by-lightning", "&aYou struck &b{name-color}{player} &awith lightning!"),
    TROLL_PLAYER_DEMO_MENU_OPENED("messages/global-messages.yml", "troll.player-demo-menu-opened", "&aYou opened the demo menu for &b{name-color}{player}&a!"),

    ;

    private final String configName;
    private final String configPath;
    private final Object defaultValue;

    GlobalMessagesLocaleImpl(String configName, String configPath, Object defaultValue) {
        this.configName = configName;
        this.configPath = configPath;
        this.defaultValue = defaultValue;
    }
}