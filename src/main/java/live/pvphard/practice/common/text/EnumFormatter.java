package live.pvphard.practice.common.text;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.arena.ArenaType;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

/**
 * @author Emmy
 * @project hard-practice
 * @since 03/09/2025
 */
@UtilityClass
public class EnumFormatter {
    /**
     * Method to get and output all values of an enum as a formatted string.
     * </br>
     * </br>
     * Example Enum: {@link ArenaType}
     * </br>
     * Appearance: Invalid arena type. Available types are SHARED, STANDALONE, FFA.
     *
     * @param enumClass The enum class to get the types from.
     * @return The available types of the enum.
     */
    public String outputAvailableValues(Class<? extends Enum<?>> enumClass) {
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        String[] enumNames = Arrays.stream(enumConstants).map(Enum::name).toArray(String[]::new);

        String availableTypes = String.join(", ", enumNames);
        String readableName = enumClassToReadable(enumClass);

        String message = HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_INVALID_TYPE)
                .replace("{type}", readableName)
                .replace("{types}", availableTypes);

        return CC.translate(message);
    }

    /**
     * Method to format an enum class name to a more readable format.
     * </br>
     *
     * </br>
     * Example Enum: {@link ArenaType}
     * </br>
     * Appearance: arena type
     *
     * @param enumClass The enum class to format the class name from.
     * @return The formatted class name.
     */
    public String enumClassToReadable(Class<? extends Enum<?>> enumClass) {
        return String.join(" ", enumClass.getSimpleName().split("(?=[A-Z])")).toLowerCase();
    }
}