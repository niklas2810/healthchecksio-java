package com.niklasarndt.healthchecksio.util;

import lombok.Getter;

@Getter
public class Pair<K, V> {

    private final K left;
    private final V right;

    public Pair(K left, V right) {
        this.left = left;
        this.right = right;
    }
}
