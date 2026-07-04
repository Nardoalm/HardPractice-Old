package live.pvphard.practice.common.logger;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.common.text.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Remi
 * @project Hard
 * @date 5/27/2024
 */
@UtilityClass
public class Logger {
    private final static ConsoleCommandSender consoleSender;
    private static final Map<UUID, Exception> storedExceptions;

    private static final String PHASE_HEADER_PREFIX = "&b&l--- ";
    private static final String PHASE_HEADER_SUFFIX = " ---";
    private static final String TASK_PREFIX_SUCCESS = "&a✔  &f";
    private static final String TASK_PREFIX_FAIL = "&c✖ &f";

    static {
        consoleSender = HardPractice.getInstance().getServer().getConsoleSender();
        storedExceptions = new HashMap<>();
    }

    /**
     * Log a message to the console.
     *
     * @param message the message to log
     */
    public void info(String message) {
        consoleSender.sendMessage(CC.translate(CC.PREFIX + message));
    }

    /**
     * Log a message to the console without any prefix.
     *
     * @param message the message to log
     */
    public void infoNoPrefix(String message) {
        consoleSender.sendMessage(CC.translate(message));
    }

    /**
     * Log an error to the console.
     *
     * @param message the error message to log
     */
    public void error(String message) {
        consoleSender.sendMessage(CC.translate(CC.ERROR_PREFIX + "&c(ERROR) &8" + message));
    }

    /**
     * Log a warning to the console.
     *
     * @param message the warning message to log
     */
    public void warn(String message) {
        consoleSender.sendMessage(CC.translate(CC.WARNING_PREFIX + "&e(WARNING) &8" + message));
    }

    /**
     * Log an exception to the console.
     *
     * @param message   the info message or the class name
     * @param exception the exception to log
     */
    public static void logException(String message, Exception exception) {
        UUID errorId = UUID.randomUUID();
        storedExceptions.put(errorId, exception);

        Arrays.asList(
                "",
                CC.ERROR_PREFIX + "&c&lEXCEPTION",
                " &f" + message + ": &r" + exception.getMessage(),
                "",
                " &c(Digite &4viewerror " + errorId + " &cno console para ver os detalhes)",
                ""
        ).forEach(line -> consoleSender.sendMessage(CC.translate(line)));
    }

    /**
     * Retrieve and print the full stack trace of a stored exception.
     *
     * @param errorId The UUID of the error.
     */
    @SuppressWarnings("all")
    public static void viewException(UUID errorId) {
        Exception exception = storedExceptions.get(errorId);
        if (exception == null) {
            consoleSender.sendMessage(CC.translate(CC.ERROR_PREFIX + "&cNenhuma excecao encontrada com o ID: " + errorId));
            return;
        }

        Arrays.asList(
                "",
                CC.MENU_BAR + CC.MENU_BAR + CC.MENU_BAR + CC.MENU_BAR,
                "",
                "&c&lVISUALIZANDO ERRO: " + errorId,
                ""
        ).forEach(line -> consoleSender.sendMessage(CC.translate(line)));

        exception.printStackTrace();

        StackTraceElement[] stackTrace = exception.getStackTrace();
        String locationMessage = "&cErro ocorrido em: local desconhecido";

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith(HardPractice.getInstance().getService(PluginConstant.class).getPackageDirectory())) {
                locationMessage = "&cErro ocorrido em: " + element.getClassName() + " (Linha " + element.getLineNumber() + ")";
                break;
            }
        }

        consoleSender.sendMessage("");
        consoleSender.sendMessage(CC.translate(locationMessage));
        consoleSender.sendMessage("");
        consoleSender.sendMessage(CC.MENU_BAR + CC.MENU_BAR + CC.MENU_BAR + CC.MENU_BAR);
        consoleSender.sendMessage("");

        storedExceptions.remove(errorId);
    }

    /**
     * Log the time it takes to run a task with clear success/failure indication.
     *
     * @param taskName the name of the task to run
     * @param runnable the task to run
     */
    public void logTime(String taskName, Runnable runnable) {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            runnable.run();
            success = true;
        } catch (Exception exception) {
            logException("Falha ao executar a tarefa " + taskName, exception);
        } finally {
            long end = System.currentTimeMillis();
            String prefix = success ? TASK_PREFIX_SUCCESS : TASK_PREFIX_FAIL;
            String message = success ? "&fInicializado com sucesso -> &b" : "&cFalha ao inicializar -> &b";
            consoleSender.sendMessage(CC.translate(prefix + message + taskName + " &fem &b" + (end - start) + " ms&f."));
        }
    }
    /**
     * Log the time it takes to run a task.
     *
     * @param runnableTaskName the name of the task to run
     * @param runnable         the task to run
     */
    public void logTimeTask(String runnableTaskName, Runnable runnable) {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            runnable.run();
            success = true;
        } catch (Exception exception) {
            logException("Falha ao executar a tarefa " + runnableTaskName, exception);
        } finally {
            long end = System.currentTimeMillis();
            String prefix = success ? TASK_PREFIX_SUCCESS : TASK_PREFIX_FAIL;
            String message = success ? "&fExecutado com sucesso -> &b" : "&cFalha ao executar -> &b";
            consoleSender.sendMessage(CC.translate( prefix + message + runnableTaskName + " &fem &b" + (end - start) + " ms&f."));
        }
    }

    /**
     * Measure the runtime of a task and log it to the console with the provided action in its parameter.
     *
     * @param action   the action
     * @param task     the task to measure
     * @param runnable the task to run
     */
    public void logTimeWithAction(String action, String task, Runnable runnable) {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            runnable.run();
            success = true;
        } catch (Exception exception) {
            logException("Falha ao " + action + " a tarefa " + task, exception);
        } finally {
            long runtime = System.currentTimeMillis() - start;
            String prefix = success ? TASK_PREFIX_SUCCESS : TASK_PREFIX_FAIL;
            String message = success ? "&fSucesso ao " + action + " &b" : "&cFalha ao " + action + " &b";
            consoleSender.sendMessage(CC.translate(prefix + message + task + " &fem &b" + runtime + "ms&f."));
        }
    }

    /**
     * Logs the start of a major initialization phase.
     * @param phaseName The name of the phase (e.g., "Service Setup Phase").
     */
    public void logPhaseStart(String phaseName) {
        consoleSender.sendMessage(CC.translate(""));
        consoleSender.sendMessage(CC.translate(PHASE_HEADER_PREFIX + phaseName.toUpperCase() + PHASE_HEADER_SUFFIX));
    }

    /**
     * Logs the completion of a major initialization phase.
     * @param phaseName The name of the phase (e.g., "Service Initialization").
     */
    public void logPhaseComplete(String phaseName) {
        consoleSender.sendMessage(CC.translate(PHASE_HEADER_PREFIX + phaseName.toUpperCase() + " CONCLUIDA" + PHASE_HEADER_SUFFIX));
        consoleSender.sendMessage(CC.translate(""));
    }

    /**
     * Sends a message to both the command sender and the console log with a standard prefix.
     *
     * @param sender  The command sender to send the message to.
     * @param message The message to send.
     */
    public void sendMessageAndLog(CommandSender sender, String message) {
        sender.sendMessage(CC.translate(CC.PREFIX + message));
        Logger.info(message);
    }
}
