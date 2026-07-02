package us.alleypvp.practice.feature.event.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.feature.event.EventDefinition;
import us.alleypvp.practice.feature.event.EventPhase;
import us.alleypvp.practice.feature.event.EventService;
import us.alleypvp.practice.feature.event.model.ActiveEvent;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&bEvents";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new LinkedHashMap<>();
        EventService eventService = AlleyPractice.getInstance().getService(EventService.class);

        int slot = 11;
        for (EventDefinition definition : eventService.getEventDefinitions()) {
            if (!definition.isEnabled()) {
                continue;
            }

            buttons.put(slot, new EventButton(definition));
            slot = this.skipIfSlotCrossingBorder(slot + 1);
        }

        if (buttons.isEmpty()) {
            buttons.put(13, Button.placeholder(Material.BARRIER, (byte) 0, CC.translate("&cNo events available")));
        }

        this.addGlass(buttons, 15);
        return buttons;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    private static final class EventButton extends Button {
        private final EventDefinition definition;

        private EventButton(EventDefinition definition) {
            this.definition = definition;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            EventService eventService = AlleyPractice.getInstance().getService(EventService.class);
            ActiveEvent activeEvent = eventService.getActiveEvent();

            List<String> lore = new ArrayList<>(this.definition.getDescription());
            lore.add("");

            if (activeEvent == null) {
                if (player.hasPermission("kaos.command.donator.host")) {
                    lore.add("&aClick to host this event.");
                } else {
                    lore.add("&7No event of this type is active.");
                    lore.add("&cOnly players with permission can host.");
                }
            } else if (activeEvent.getDefinition().getKey().equalsIgnoreCase(this.definition.getKey())) {
                lore.add("&fStatus: " + (activeEvent.getPhase() == EventPhase.RUNNING ? "&cIn progress" : "&aWaiting"));
                lore.add("&fPlayers: &b" + activeEvent.getParticipants().size() + "/" + activeEvent.getDefinition().getMaximumPlayers());

                if (activeEvent.isParticipant(player.getUniqueId())) {
                    lore.add("&cClick to leave the event.");
                } else if (activeEvent.getPhase() == EventPhase.RUNNING) {
                    lore.add("&cThis event has already started.");
                } else {
                    lore.add("&aClick to join the event.");
                }
            } else {
                lore.add("&cAnother event is already active: " + activeEvent.getDefinition().getDisplayName());
            }

            return new ItemBuilder(this.definition.getMaterial())
                    .name(this.definition.getDisplayName())
                    .durability(this.definition.getDurability())
                    .lore(lore)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) {
                return;
            }

            EventService eventService = AlleyPractice.getInstance().getService(EventService.class);
            ActiveEvent activeEvent = eventService.getActiveEvent();

            if (activeEvent == null) {
                if (!player.hasPermission("kaos.command.donator.host")) {
                    player.sendMessage(CC.translate("&cThere is no active event at the moment."));
                    this.playFail(player);
                    return;
                }

                if (eventService.hostEvent(player, this.definition.getKey())) {
                    this.playSuccess(player);
                } else {
                    this.playFail(player);
                }
                return;
            }

            if (!activeEvent.getDefinition().getKey().equalsIgnoreCase(this.definition.getKey())) {
                player.sendMessage(CC.translate("&cAnother event is already active at the moment."));
                this.playFail(player);
                return;
            }

            if (activeEvent.isParticipant(player.getUniqueId())) {
                if (eventService.leaveActiveEvent(player, false)) {
                    this.playSuccess(player);
                } else {
                    this.playFail(player);
                }
                return;
            }

            if (eventService.joinActiveEvent(player)) {
                this.playSuccess(player);
            } else {
                this.playFail(player);
            }
        }
    }
}