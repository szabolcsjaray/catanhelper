package com.jeec.game;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jeec.game.GameResourceType;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameTileType {
    FOREST("Erdő", GameResourceType.WOOD, "fa.png"),
    WHEATFIELD("Búzamező", GameResourceType.WHEAT, "buza.png"),
    MOUNTAIN("Hegy", GameResourceType.ROCK, "szikla.png"),
    CLAY("Agyag", GameResourceType.BRICK, "tegla.png"),
    MEADOW("Mező", GameResourceType.SHEEP, "birka.png"),
    DESERT("Sivatag", GameResourceType.NONE, "sivatag.png");

    private GameResourceType resourceType;
    private String name;
    private String picture;

    private GameTileType(final String name, final GameResourceType resourceType, final String picture) {
        this.name = name;
        this.resourceType = resourceType;
        this.setPicture(picture);
    }

    public String getName() {
        return name;
    }

    public static List<GameTileType> getAllTileTypes() {

        List<GameTileType> enumValues = new ArrayList<>();
        Object[] possibleValues = FOREST.getDeclaringClass().getEnumConstants();
        for (int i=0;i<possibleValues.length;i++) {
            enumValues.add((GameTileType)possibleValues[i]);
        }
        return enumValues;
    }

    public GameResourceType getResourceType() {
        return resourceType;
    }

    public String getLabel() {
        return this.toString();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
