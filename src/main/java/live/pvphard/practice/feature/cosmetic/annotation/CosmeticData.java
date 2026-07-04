package live.pvphard.practice.feature.cosmetic.annotation;

import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CosmeticData {
    CosmeticType type();

    String name();

    String description();

    String permission() default "";

    Material icon();

    int slot();

    int price() default 500;
}
