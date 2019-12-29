package com.jeec.game;

import java.util.ArrayList;
import java.util.List;

public class OrderRange {
    private int from;
    private int to;

    List<Integer> playerIds;

    public OrderRange() {
        playerIds = new ArrayList<>();
    }

    public OrderRange(int from, int to) {
        this();
        this.from = from;
        this.to = to;
    }

    public List<Integer> getPlayerIds() {
        return playerIds;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void addPlayerId(int playerId) {
        playerIds.add(playerId);
    }
}
