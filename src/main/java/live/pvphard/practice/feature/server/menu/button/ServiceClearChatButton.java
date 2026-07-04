package live.pvphard.practice.feature.server.menu.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class ServiceClearChatButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .name("&c&lLimpar Chat")
                .lore(
                        "&fIsso limpará o chat",
                        "&fde todos os jogadores do servidor.",
                        "",
                        "&cClique para limpar!"
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        HardPractice.getInstance().getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            for (int i = 0; i < 1500; i++) {
                onlinePlayer.sendMessage(this.getRandomizedCharacters());
            }

            this.getString(GlobalMessagesLocaleImpl.CHAT_CLEARED_BY_STAFF);
        });
    }

    /**
     * Generates a randomized string of invisible characters for clearing the chat.
     * Mainly to prevent client side bypasses whose stack same messages.
     *
     * @return the string of randomized characters.
     */
    private String getRandomizedCharacters() {
        StringBuilder line = new StringBuilder();
        int randomLength = ThreadLocalRandom.current().nextInt(5) + 1;

        for (int j = 0; j < randomLength; j++) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                line.append(" ");
            } else {
                line.append("  ");
            }
        }

        return line.toString();
    }
}
