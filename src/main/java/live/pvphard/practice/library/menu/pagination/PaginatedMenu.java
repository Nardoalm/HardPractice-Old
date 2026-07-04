package live.pvphard.practice.library.menu.pagination;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.library.menu.impl.PageGlassButton;
import live.pvphard.practice.library.menu.pagination.impl.button.PageButton;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public abstract class PaginatedMenu extends Menu {

    private int page = 1;

    {
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player);
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod    delta to modify the page number by
     */
    public final void modPage(Player player, int mod) {
        page += mod;
        getButtons().clear();
        openMenu(player);
    }

    /**
     * @param player player viewing the inventory
     */
    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage());
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage());
        int maxIndex = (int) ((double) (page) * getMaxItemsPerPage());
        int topIndex = 0;

        HashMap<Integer, Button> buttons = new HashMap<>();

        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (getMaxItemsPerPage()) * (page - 1)) - 9;
                buttons.put(ind, entry.getValue());

                if (ind > topIndex) {
                    topIndex = ind;
                }
            }
        }

        Button previousPageButton = getPreviousPageButton(player);
        if (previousPageButton != null) {
            buttons.put(getPreviousPageButtonSlot(), previousPageButton);
        }

        Button nextPageButton = getNextPageButton(player);
        if (nextPageButton != null) {
            buttons.put(getNextPageButtonSlot(), nextPageButton);
        }


        Map<Integer, Button> global = getGlobalButtons(player);

        if (global != null) {
            buttons.putAll(global);
        }

        return buttons;
    }

    /**
     * Method to get the maximum number of items that can be displayed on a single page.
     *
     * @return the maximum number of items per page.
     */
    public int getMaxItemsPerPage() {
        return 36;
    }

    /**
     * Validates the slot number to ensure it does not conflict with reserved slots.
     * Reserved slots are typically used for glass panes or other UI elements.
     *
     * @param slot the original slot number to validate
     * @return a valid slot number that does not conflict with reserved slots
     */
    public int validateSlot(int slot) {
        int slotsPerPage = 36;

        List<Integer> baseSlotsToAvoid = Arrays.asList(0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36);

        int page = slot / slotsPerPage;

        int pageOffset = page * slotsPerPage;

        Set<Integer> slotsToAvoid = new HashSet<>();
        for (int baseSlot : baseSlotsToAvoid) {
            slotsToAvoid.add(baseSlot + pageOffset);
        }

        while (slotsToAvoid.contains(slot)) {
            slot++;
        }

        return slot;
    }

    /**
     * Adds glass panes to the inventory to avoid reserved slots.
     * This method fills the reserved slots with glass buttons to ensure they are not used for other items.
     *
     * @param buttons a map of buttons representing the inventory slots
     */
    public void addGlassToAvoidedSlots(Map<Integer, Button> buttons) {
        int slotsPerPage = getMaxItemsPerPage();
        List<Integer> baseSlotsToAvoid = Arrays.asList(0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36);

        for (int page = 0; page <= (buttons.size() / slotsPerPage); page++) {
            int pageOffset = page * slotsPerPage;

            for (int baseSlot : baseSlotsToAvoid) {
                int slot = baseSlot + pageOffset;
                if (!buttons.containsKey(slot)) {
                    buttons.put(slot, new PageGlassButton(15));
                }
            }
        }
    }

    /**
     * @param player player viewing the inventory
     * @return a Map of button that returns items which will be present on all pages
     */
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    public int getPreviousPageButtonSlot() {
        return 0;
    }

    public int getNextPageButtonSlot() {
        return 8;
    }

    public Button getPreviousPageButton(Player player) {
        return new PageButton(this, -1);
    }

    public Button getNextPageButton(Player player) {
        return new PageButton(this, 1);
    }

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    public abstract String getPrePaginatedTitle(Player player);

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player player);

}
