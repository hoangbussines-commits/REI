package com.realisticrecipe.item.reader;

public enum ReaderMode {
    ENERGY("Energy Reader"),
    ENTITY("Entity Reader");

    public final String displayName;

    ReaderMode(String displayName) {
        this.displayName = displayName;
    }

    public ReaderMode next() {
        return values()[(ordinal() + 1) % values().length];
    }
}