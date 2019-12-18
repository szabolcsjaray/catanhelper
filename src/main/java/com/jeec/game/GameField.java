package com.jeec.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameField {
    private String name;
    private List<GameTile> gameTiles;
    static int[] rowRanges = { -1, 1, -1, 2, -2, 2, -1, 2, -1, 1 };

    public void generateGameField() {
        gameTiles = new ArrayList<>();
        for (GameTileType gameTileType : GameTile.CATAN_TILES) {
            GameTile newTile = new GameTile(gameTileType, 0, 0);
            gameTiles.add(newTile);
        }
        Collections.shuffle(gameTiles);
        Iterator<GameTile> tileIterator = gameTiles.iterator();
        int rowRangePos = 0;

        for (int y = -2; y <= 2; y++) {
            for (int x = rowRanges[rowRangePos]; x <= rowRanges[rowRangePos + 1]; x++) {
                GameTile tile = tileIterator.next();
                tile.setX(x);
                tile.setY(y);
            }
            rowRangePos+=2;
        }
    }

    public List<GameTile> getGameTiles() {
        return gameTiles;
    }

    public void setGameTiles(List<GameTile> gameTiles) {
        this.gameTiles = gameTiles;
    }

    public GameField() {
        setName("field name");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
