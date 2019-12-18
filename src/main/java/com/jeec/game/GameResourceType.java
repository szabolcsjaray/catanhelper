package com.jeec.game;

public enum GameResourceType {
    WOOD("Fa"),
    WHEAT("Búza"),
    BRICK("Tégla"),
    SHEEP("Birka"),
    ROCK("Szikla"),
    NONE("Semmi");

    private String name;
    private GameResourceType(final String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
