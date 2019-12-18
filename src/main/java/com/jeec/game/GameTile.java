package com.jeec.game;

import java.util.ArrayList;
import java.util.List;
import com.jeec.game.GameTileType;

public class GameTile {

    public static GameTileType[] CATAN_TILES = {
            GameTileType.FOREST,
            GameTileType.FOREST,
            GameTileType.FOREST,
            GameTileType.FOREST,
            GameTileType.CLAY,
            GameTileType.CLAY,
            GameTileType.CLAY,
            GameTileType.MOUNTAIN,
            GameTileType.MOUNTAIN,
            GameTileType.MOUNTAIN,
            GameTileType.MEADOW,
            GameTileType.MEADOW,
            GameTileType.MEADOW,
            GameTileType.MEADOW,
            GameTileType.WHEATFIELD,
            GameTileType.WHEATFIELD,
            GameTileType.WHEATFIELD,
            GameTileType.WHEATFIELD,
            GameTileType.DESERT
    };

    private GameTileType type;
    private int x;
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    private int y;

    public GameTile(GameTileType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public GameTileType getType() {
        return type;
    }
    public void setType(GameTileType type) {
        this.type = type;
    }


}
