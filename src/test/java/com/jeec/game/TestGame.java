package com.jeec.game;

import com.jeec.game.ColorCode;
import com.jeec.game.Game;
import com.jeec.game.GameState;
import com.jeec.game.Player;
import com.jeec.game.PlayerState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGame {
    private static final String PLAYER3 = "Jane";
    private static final String PLAYER2 = "Jack";
    private static final String PLAYER1 = "Joe";
    private Game game;
    static final String DEV_HASH = "AA";
    int player1Id, player2Id, player3Id;

    @Before
    public void initTests() {
        this.game = new Game();
    }

    @Test
    public void testAddPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        Assert.assertEquals(res, "OK");
        Assert.assertEquals(ColorCode.values().length-1, game.getAvailableColors().size());
        Assert.assertNull(game.getAvailableColors().get(ColorCode.BLUE.name()));
        long pHash = this.game.getPlayer(0).getPlayerHash();
        Assert.assertTrue(pHash>=10000 && pHash<100000);
    }

    @Test
    public void testAvailableColors() {
        Assert.assertEquals(ColorCode.values().length, this.game.getAvailableColors().size());
    }

    @Test
    public void testAddSamePlayerName() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.ORANGE.name());
        Assert.assertNotEquals("OK", res);

    }

    @Test
    public void testAddSameColor() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, "Jill", ColorCode.BLUE.name());
        Assert.assertNotEquals("OK", res);

    }

    @Test
    public void testAddPlayerNameWrongColor() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, "ilyen sz\u00edn nincs");
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testAddPlayerNameWrongDevice() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer("WRONGHASH", PLAYER1, ColorCode.ORANGE.name());
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testGetPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        Player player = this.game.getPlayer(0);
        Assert.assertNotNull(player);
        Assert.assertEquals(PLAYER1, player.getName());
    }

    @Test
    public void testAddDevice() {
        String res = this.game.addDevice("AAA33FF");
        Assert.assertEquals(res, "OK");
    }

    @Test
    public void testAddSameDeviceHash() {
        String res = this.game.addDevice("AAA33FF");
        res = this.game.addDevice("AAA33FF");
        Assert.assertNotEquals(res, "OK");
    }

    private void initPlayers() {
    	this.game = new Game();
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER2, ColorCode.RED.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER3, ColorCode.ORANGE.name());
        player1Id = this.game.getPlayers().get(PLAYER1).getPlayerId();
        player2Id = this.game.getPlayers().get(PLAYER2).getPlayerId();
        player3Id = this.game.getPlayers().get(PLAYER3).getPlayerId();

    }

    @Test
    public void testFindPlayer() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        Assert.assertEquals(1L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
    }

    @Test
    public void testPlayerUp() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        this.game.playerUp(player);
        Assert.assertEquals(0L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
        Assert.assertEquals(PLAYER1, this.game.findPlayerByOrder(1).getName());
    }

    @Test
    public void testPlayerDown() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        this.game.playerDown(player);
        Assert.assertEquals(2L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
        Assert.assertEquals(PLAYER3, this.game.findPlayerByOrder(1).getName());
    }

    @Test
    public void testGameStarted() {
        this.initPlayers();
        this.game.startGame();
        Assert.assertEquals(GameState.ORDER_ROLLINGS, this.game.getState());
        Assert.assertEquals(PlayerState.ROLLING_ORDER, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER, this.game.getPlayer(player3Id).getState());
        //Assert.assertEquals(true, this.game.getPlayer(player1Id).isTeller());
    }

    @Test
    public void testFirstOrderRoll() {
        this.initPlayers();
        this.game.startGame();
        this.game.orderDiceRolled(player1Id, 9 );
        Assert.assertEquals(9, this.game.getPlayer(player1Id).getLastRoll());
        Assert.assertEquals(PlayerState.WAITING_FOR_ORDER_ROLL_VERIFICATION, this.game.getPlayer(player1Id).getState());
    }

    @Test
    public void testFirstOrderRollVerifed() {
        this.initPlayers();
        this.game.startGame();
        this.game.orderDiceRolled(player1Id, 9 );
        this.game.orderDiceRollVerified();
        Assert.assertEquals(PlayerState.ORDER_ROLLED, this.game.getPlayer(player1Id).getState());

    }

    @Test
    public void testFirstOrderRollWrong() {
        this.initPlayers();
        this.game.startGame();
        this.game.orderDiceRolled(player1Id, 9 );
        this.game.orderDiceRollWrong();
        Assert.assertEquals(PlayerState.ROLLING_ORDER_AGAIN, this.game.getPlayer(player1Id).getState());

    }

    @Test
    public void testEveryOneRolledOrder() {
        this.initPlayers();
        this.game.startGame();
        this.game.orderDiceRolled(player1Id, 9 );
        this.game.orderDiceRollVerified();
        this.game.orderDiceRolled(player2Id, 11 );
        this.game.orderDiceRollVerified();
        Assert.assertEquals(PlayerState.ORDER_ROLLED, this.game.getPlayer(player2Id).getState());
        this.game.orderDiceRolled(player3Id, 10 );
        this.game.orderDiceRollVerified();
        Assert.assertEquals(GameState.INITIAL_BUILDING, this.game.getState());
        Assert.assertEquals(PlayerState.INITIAL_BUILD, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHER_INITIAL_BUILD, this.game.getPlayer(player3Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHER_INITIAL_BUILD, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(1, this.game.getPlayer(player2Id).getInitialOrder());
        Assert.assertEquals(2, this.game.getPlayer(player3Id).getInitialOrder());
        Assert.assertEquals(3, this.game.getPlayer(player1Id).getInitialOrder());

    }

    @Test
    public void testSameRollOrder() {
        this.initPlayers();
        this.game.startGame();
        this.game.orderDiceRolled(player1Id, 9 );
        this.game.orderDiceRollVerified();
        this.game.orderDiceRolled(player2Id, 9 );
        this.game.orderDiceRollVerified();
        Assert.assertEquals(PlayerState.ORDER_ROLLED, this.game.getPlayer(player2Id).getState());
        this.game.orderDiceRolled(player3Id, 10 );
        this.game.orderDiceRollVerified();
        Assert.assertEquals(GameState.ORDER_ROLLINGS, this.game.getState());
        Assert.assertEquals(1, this.game.getPlayer(player3Id).getInitialOrder());
        Assert.assertEquals(0, this.game.getPlayer(player1Id).getInitialOrder());
        Assert.assertEquals(0, this.game.getPlayer(player2Id).getInitialOrder());

        //Assert.assertEquals(PlayerState.ORDER_ROLLED, this.game.getPlayer(player3Id).getState());
        assertPlayerState(PlayerState.ORDER_ROLLED, player3Id);
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER_FIGHT, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.ROLLING_ORDER_FIGHT, this.game.getPlayer(player1Id).getState());

        this.game.orderDiceRolled(player1Id, 4 );
        assertPlayerState(PlayerState.WAITING_FOR_ORDER_ROLL_FIGHT_VERIFICATION, player1Id);
        Assert.assertEquals(4,  this.game.getPlayer(player1Id).getLastRoll());
        this.game.orderDiceRollVerified();
        assertPlayerState(PlayerState.ORDER_ROLLED, player1Id);
        this.game.orderDiceRolled(player2Id, 2 );
        this.game.orderDiceRollVerified();

        Assert.assertEquals(GameState.INITIAL_BUILDING, this.game.getState());
    }

    private void assertPlayerState(PlayerState state, int playerId) {
        Assert.assertEquals(state, this.game.getPlayer(playerId).getState());

    }
}