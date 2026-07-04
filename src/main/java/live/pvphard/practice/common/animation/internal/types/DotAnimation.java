package live.pvphard.practice.common.animation.internal.types;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
public class DotAnimation extends Animation {

    public DotAnimation() {
        super(Arrays.asList(".", "..", "..."), 500);
    }
}