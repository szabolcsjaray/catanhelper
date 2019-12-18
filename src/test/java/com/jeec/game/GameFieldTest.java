package com.jeec.game;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameFieldTest {

    @Test
    public void testGenerateField() {
        GameField gameField = new GameField();
        gameField.generateGameField();

        assertEquals(19, gameField.getGameTiles().size());

    }

}
