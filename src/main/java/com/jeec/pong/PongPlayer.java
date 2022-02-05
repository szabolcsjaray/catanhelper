package com.jeec.pong;

import java.util.ArrayList;
import java.util.List;

public class PongPlayer {
    private String name;
    private String device;

    private int points;
    private int matches;
    private int wins;
    private List<String> calledBy;
    private List<String> calledToPlay;

    public PongPlayer() {
        this.points = 0;
        this.matches = 0;
        this.device = "";
        this.name = "";
        this.calledBy = new ArrayList<>();
        this.calledToPlay = new ArrayList<>();
    }

    public List<String> getCalledToPlay() {
        return calledToPlay;
    }

    public void setCalledToPlay(List<String> calledToPlay) {
        this.calledToPlay = calledToPlay;
    }

    public List<String> getCalledBy() {
        return calledBy;
    }

    public void setCalledBy(List<String> calledBy) {
        this.calledBy = calledBy;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCalled(String playerDev) {
        calledToPlay.add(playerDev);
    }

    public void addCalledBy(String playerDev) {
        calledBy.add(playerDev);
    }
}
