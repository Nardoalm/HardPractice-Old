package live.pvphard.practice.common.collection.internal;

import live.pvphard.practice.common.collection.Triple;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a mutable container for three objects of potentially different types.
 * Provides a simple implementation for a collection tuple.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 * @param <C> the type of the third object
 * @author Remi
 * @project Hard
 * @date 5/27/2024
 */
@Getter
@Setter
public class MutableTriple<A, B, C> implements Triple<A, B, C> {
    private A a;
    private B b;
    private C c;

    /**
     * Constructor for the MutableTriple class.
     *
     * @param a the first object
     * @param b the second object
     * @param c the third object
     */
    public MutableTriple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}