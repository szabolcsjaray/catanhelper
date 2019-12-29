package com.jeec.game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPlayer {

    private static String NAME = "pname";
    private static String DEVHASH = "devh";


    Player tested;

    @Before
    public void initPlayer() {
        tested = new Player(NAME, ColorCode.BLUE, DEVHASH, 0);
    }

    @Test
    public void testPlayerRolledDice() {
        tested.orderDiceRolled(8);
        Assert.assertEquals( 8, tested.getLastRoll());
        Assert.assertEquals(PlayerState.WAITING_FOR_ORDER_ROLL_VERIFICATION, tested.getState());
    }

    @Test
    public void testOrderDiceRollVerified() {
        tested.orderDiceRollVerified();
        Assert.assertEquals(PlayerState.ORDER_ROLLED, tested.getState());
    }

    @Test
    public void testOrderDiceRollWrong() {
        tested.orderDiceRollWrong();
        Assert.assertEquals(PlayerState.ROLLING_ORDER_AGAIN, tested.getState());
    }
}
