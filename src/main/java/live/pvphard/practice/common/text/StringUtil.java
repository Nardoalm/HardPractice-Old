package live.pvphard.practice.common.text;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    /**
     * A helper method to convert any enum constants name into a user-friendly, title-cased string.
     * Example: SOME_ENUM_CONSTANT -> Some Enum Constant
     *
     * @param anEnum The enum constants to format.
     * @return A formatted, title-cased string.
     */
    public String formatEnumName(Enum<?> anEnum) {
        String lowerCase = anEnum.name().replace('_', ' ').toLowerCase();

        String[] words = lowerCase.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
}
