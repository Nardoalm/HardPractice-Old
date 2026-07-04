package live.pvphard.practice.library.menu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Remi
 * @project hard-practice
 * @date 22/07/2025
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MenuIdentifier {
    /**
     * The identifier for the menu.
     * This should be unique across all menus.
     *
     * @return the unique identifier for the menu
     */
    String value();
}
