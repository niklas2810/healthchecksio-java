package com.niklasarndt.healthchecksio.util;

import lombok.Getter;

/**
 * <p>A generic representation of a bundle of two values, since native Java
 * doesnt have one.</p>
 *
 * @param <K> The content type of the left value.
 * @param <V> The content type of the right value.
 *
 * @since 1.0.1
 */
@Getter
public class Pair<K, V> {

    private final K left;
    private final V right;

    public Pair(K left, V right) {
        this.left = left;
        this.right = right;
    }
}
