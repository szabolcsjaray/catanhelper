package com.jeec.game;

import com.jeec.game.ColorCode;
import com.jeec.game.Device;
import com.jeec.game.GameState;
import com.jeec.game.Player;
import com.jeec.game.PlayerState;
import com.jeec.game.log.GameLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.util.MultiValueMap;

public class Game {
    private int stateVersion = 0;
    private GameState state = GameState.WAITING_FOR_PLAYERS;
    private Map<String, Player> players = new HashMap<String, Player>();
    private Map<String, Device> devices = new HashMap<String, Device>();
    private Map<String, String> availableColors = new HashMap<>();
    private GameLog gameLog = null;
    private List<OrderRange> orderRanges;

	private int round;
    private boolean playersLocked;
    private GameField gameField;
    private int nextInitialBuilderOrder;
    private int initialBuildRound;

    public Game() {
    	Player.resetPlayerIds();
        initAllColors();
        gameLog =  new GameLog(this);
    }

    private void initAllColors() {
		for (ColorCode color: ColorCode.values()) {
			availableColors.put(color.name(), color.getColorStr());
		}
	}

    public Map<String, String> getAvailableColors() {
        return availableColors;
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String addPlayer(String deviceHash, String playerName, String colorName) {
        if (ColorCode.findColor(colorName) == null) {
            return "Nincs ilyen sz\u00edn.";
        }
        String checkResult = this.checkIfNameOrColorExists(playerName, colorName);
        if (!checkResult.isEmpty()) {
            return checkResult;
        }
        checkResult = this.checkIfDeviceExists(deviceHash);
        if (!checkResult.isEmpty()) {
            return checkResult;
        }
        Player player = new Player(playerName, ColorCode.findColor(colorName), deviceHash, this.players.size());
        uniquePlayerHash(player);
        this.availableColors.remove(ColorCode.findColor(colorName).name());
        this.players.put(playerName, player);
        this.devices.get(deviceHash).addPlayer(player);
        ++this.stateVersion;
        return "OK";
    }

    private void uniquePlayerHash(Player playerToCheck) {
        long sameHash;
        long pHash = playerToCheck.getPlayerHash();
        do {
            playerToCheck.setPlayerHash(Player.createPlayerHash());
            sameHash = this.players.values().stream().filter(player -> pHash==player.getPlayerHash()).count();
        } while (sameHash>1);
    }

    private String checkIfDeviceExists(String deviceHash) {
        if (this.devices.containsKey(deviceHash)) {
            return "";
        }
        return "Ez az eszk\u00f6z (mobil, tablet) m\u00e9g nem lett csatlakoztatva.";
    }

    private String checkIfNameOrColorExists(String playerName, String colorName) {
        for (String name : this.players.keySet()) {
            if (name.equals(playerName)) {
                return "A j\u00e1t\u00e9kos n\u00e9v ("+playerName+") m\u00e1r foglalt.";
            }
            if (this.players.get(name).getColor().getColorName().equals(ColorCode.findColor(colorName).getColorName())) {
                return "A sz\u00edn m\u00e1r foglalt.";
            }
        }
        return "";
    }

    public String addDevice(String deviceHash) {
        if (this.findDevice(deviceHash)) {
            return "Az eszk\u00f6z m\u00e1r csatlakoztatva van";
        }
        ++this.stateVersion;
        this.devices.put(deviceHash, new Device(deviceHash));
        return "OK";
    }

    private boolean findDevice(String deviceHash) {
        return this.devices.keySet().contains(deviceHash);
    }

    public Player getPlayer(int playerId) {
        int id = 0;
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getPlayerId() == playerId) {
                return this.players.get(name);
            }
            ++id;
        }
        return null;
    }

    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Map<String, Device> getDevices() {
        return this.devices;
    }

    public void setDevices(Map<String, Device> devices) {
        this.devices = devices;
    }

    public int getStateVersion() {
        return this.stateVersion;
    }

    public void setStateVersion(int stateVersion) {
        this.stateVersion = stateVersion;
    }

    public void increaseStateVersion() {
        ++this.stateVersion;
    }

    private void calculatePoints() {
    }

    private void resetRoundPoints() {
        for (String name : this.players.keySet()) {
            this.players.get(name).setRoundPoint(0);
        }
    }

    private void addPointsForOtherPlayersGuess(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() == teller.getMyCard()) continue;
            Player otherPlayer = this.findPlayerByCard(player.getMyChoice());
            otherPlayer.addPoint(1);
        }
    }

    private Player findPlayerByCard(int card) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getMyCard() != card) continue;
            return player;
        }
        return null;
    }

    private void addPointsForThoseWhoFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() != teller.getMyCard()) continue;
            player.addPoint(3);
        }
    }

    private boolean atLeastSomeoneFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() != teller.getMyCard()) continue;
            return true;
        }
        return false;
    }

    private void everybodyGotTwoPointsExpectTeller(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller) continue;
            player.addPoint(2);
        }
    }

    private boolean checkIfEveryPlayerFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() == teller.getMyCard()) continue;
            return false;
        }
        return true;
    }



    public void playerUp(Player player) {
        if (!this.playersLocked) {
            this.playersLocked = true;
            Player otherPlayer;
            otherPlayer = this.findPlayerByOrder(player.getPlayerOrder() - 1);
            if (player.getPlayerOrder() > 0 && (otherPlayer != null)) {
                int pOrder = player.getPlayerOrder();
                player.decreasePlayerOrder();
                otherPlayer.increasePlayerOrder();
                ++this.stateVersion;
            }
            this.playersLocked = false;
        }
    }

    public void playerDown(Player player) {
        if (!this.playersLocked) {
            this.playersLocked = true;
            Player otherPlayer;
            otherPlayer = this.findPlayerByOrder(player.getPlayerOrder() + 1);
            if ((player.getPlayerOrder() < players.size()-1) && (otherPlayer != null)) {
                int pOrder = player.getPlayerOrder();
                player.increasePlayerOrder();
                otherPlayer.decreasePlayerOrder();
                ++this.stateVersion;
            }
            this.playersLocked = false;
        }
    }

    public Player findPlayerByOrder(int order) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getPlayerOrder() != order) continue;
            return player;
        }
        return null;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Integer startGame() {
        switchToOrderRollings();
        gameLog.logGameStateChange();
        ++this.stateVersion;
        return this.players.size();
    }

    private void switchToOrderRollings() {
        this.state = GameState.ORDER_ROLLINGS;
        boolean first = true;
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (first) {
                player.setState(PlayerState.ROLLING_ORDER);
                first = false;
            } else {
                player.setState(PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER);
            }
        }

        orderRanges = new ArrayList<>();
        OrderRange fullRange = new OrderRange(1, players.size());
        players.values().forEach(player -> fullRange.addPlayerId(player.getPlayerId()));
        orderRanges.add(fullRange);
    }

    public GameLog getGameLog() {
        return gameLog;
    }

    public int getTellerId() {
        for (Player player:players.values()) {
            if (player.isTeller()) {
                return player.getPlayerId();
            }
        }
        return -1;
    }

    public GameField getGameField() {
        if (gameField==null) {
            gameField = new GameField();
            gameField.generateGameField();
        }
        return this.gameField;
    }

    public void orderDiceRolled(int playerId, int roll) {
        getPlayer(playerId).orderDiceRolled(roll);
    }

    public void orderDiceRollVerified() {
        Player next = null;
        boolean roll_fight = false;
        for(Player player:players.values()) {
            if (player.getState()==PlayerState.WAITING_FOR_ORDER_ROLL_VERIFICATION) {
                player.orderDiceRollVerified();
            } else if (player.getState()==PlayerState.WAITING_FOR_ORDER_ROLL_FIGHT_VERIFICATION) {
                player.orderDiceRollVerified();
                roll_fight = true;
            } else if (next==null &&
                    (player.getState()==PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER ||
                     player.getState()==PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER_FIGHT)) {
                next = player;
            }
        }
        if (next==null) {
            checkAndSetinitialOrder();
            if (orderRanges.size()>0) {
                return;
            }
            switchToInitialBuilding();
        } else {
            if (roll_fight) {
                next.setState(PlayerState.ROLLING_ORDER_FIGHT);
            } else {
                next.setState(PlayerState.ROLLING_ORDER);
            }
        }
    }

    private void switchToInitialBuilding() {
        this.state = GameState.INITIAL_BUILDING;
        for(Player player:players.values()) {
            player.setState(PlayerState.WAITING_FOR_OTHER_INITIAL_BUILD);
        }
        nextInitialBuilderOrder = 1;
        initialBuildRound = 1;
        Player firstBuilder = findNextInitialBuilder();
        firstBuilder.setState(PlayerState.INITIAL_BUILD);
    }

    private void goThroughRange(final OrderRange orderRange, List<OrderRange> newOrderRanges) {
        int order = orderRange.getFrom();
        for(int roll=12;roll>0;roll--) {
            final int innerRoll = roll;
            List<Player> playersForRoll = new ArrayList<>();
            players.values().stream().
                filter(player -> (player.getInitialOrder()==0 && player.getLastRoll()==innerRoll)).
                forEach(rollPlayer -> playersForRoll.add(rollPlayer));
            if (playersForRoll.size()==1) {
                playersForRoll.get(0).setInitialOrder(order);
                order++;
            } else if (playersForRoll.size()>1) {
                OrderRange newRange = new OrderRange(order, order+playersForRoll.size()-1);
                playersForRoll.forEach(player -> {
                    newRange.addPlayerId(player.getPlayerId());
                    player.setState(PlayerState.WAITING_FOR_OTHERS_ROLL_ORDER_FIGHT);
                });
                newOrderRanges.add(newRange);
                order = order+playersForRoll.size();
            }
        }
    }

    private void checkAndSetinitialOrder() {
        List<OrderRange> newOrderRanges = new ArrayList<>();
        orderRanges.forEach(orderRange -> goThroughRange(orderRange, newOrderRanges));
        orderRanges = newOrderRanges;
        if (orderRanges.size()>0) {
            getPlayer(orderRanges.get(0).playerIds.get(0)).setState(PlayerState.ROLLING_ORDER_FIGHT);
        }
    }

    private Player findNextInitialBuilder() {
        return players.values().stream().
                filter(player -> player.getInitialOrder()==nextInitialBuilderOrder).
                findAny().
                orElse(null);
    }

    public void orderDiceRollWrong() {
        for(Player player:players.values()) {
            if (player.getState()==PlayerState.WAITING_FOR_ORDER_ROLL_VERIFICATION) {
                player.orderDiceRollWrong();
            }
        }
    }

}
