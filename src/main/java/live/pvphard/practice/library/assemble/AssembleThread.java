package live.pvphard.practice.library.assemble;

import live.pvphard.practice.common.logger.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;

public class AssembleThread extends Thread {
    private final AssembleServiceImpl assembleServiceImpl;

    AssembleThread(AssembleServiceImpl assembleServiceImpl) {
        this.assembleServiceImpl = assembleServiceImpl;
        this.start();
    }

    @Override
    @SuppressWarnings("all")
    public void run() {
        while (!this.isInterrupted()) {
            try {
                tick();
                Thread.sleep(this.assembleServiceImpl.getTicks() * 50);
            } catch (InterruptedException exception) {
                this.interrupt();
                break;
            } catch (Exception exception) {
                Logger.error("Ocorreu um erro na Thread do Assemble.");
                exception.printStackTrace();
            }
        }
    }

    private void tick() {
        for (Player player : this.assembleServiceImpl.getPlugin().getServer().getOnlinePlayers()) {
            try {
                AssembleBoard board = this.assembleServiceImpl.getBoards().get(player.getUniqueId());
                if (board == null) continue;

                Scoreboard scoreboard = board.getScoreboard();
                Objective objective = board.getObjective();
                if (scoreboard == null || objective == null) continue;

                String title = ChatColor.translateAlternateColorCodes('&', this.assembleServiceImpl.getAdapter().getTitle(player));

                if (!objective.getDisplayName().equals(title)) {
                    objective.setDisplayName(title);
                }

                List<String> newLines = this.assembleServiceImpl.getAdapter().getLines(player);
                if (newLines == null || newLines.isEmpty()) {
                    board.getEntries().forEach(AssembleBoardEntry::remove);
                    board.getEntries().clear();
                } else {
                    if (newLines.size() > 15) {
                        newLines = newLines.subList(0, 15);
                    }

                    if (!this.assembleServiceImpl.getAssembleStyle().isDescending()) {
                        Collections.reverse(newLines);
                    }

                    if (board.getEntries().size() > newLines.size()) {
                        for (int i = newLines.size(); i < board.getEntries().size(); i++) {
                            AssembleBoardEntry entry = board.getEntryAtPosition(i);

                            if (entry != null) {
                                entry.remove();
                            }
                        }
                    }

                    int cache = this.assembleServiceImpl.getAssembleStyle().getStartNumber();
                    for (int i = 0; i < newLines.size(); i++) {
                        AssembleBoardEntry entry = board.getEntryAtPosition(i);

                        String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));
                        if (entry == null) {
                            entry = new AssembleBoardEntry(board, line, i);
                        }

                        entry.setText(line);
                        entry.setup();
                        entry.send(
                                this.assembleServiceImpl.getAssembleStyle().isDescending() ? cache-- : cache++
                        );
                    }
                }

                if (player.getScoreboard() != scoreboard && !assembleServiceImpl.isHook()) {
                    player.setScoreboard(scoreboard);
                }
            } catch (Exception exception) {
                if (!(exception instanceof InterruptedException)) {
                    exception.printStackTrace();
                    throw new AssembleException("Ocorreu um erro ao atualizar a scoreboard de " + player.getName() + ".");
                }
            }
        }
    }
}