package com.jeec.pong;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PongGame {
    private List<PongPlayer> pongPlayers = new ArrayList<>();
    private PongState state = PongState.BEFORE_GAME;
    int version = 0;
    private AtomicDouble ly, ry, bx, by;
    private AtomicDouble bvx, bvy;
    private PongHeartBeat heartBeat;

    public PongGame() {
        ly = new AtomicDouble();
        ry = new AtomicDouble();
        bx = new AtomicDouble();
        by = new AtomicDouble();
        bvx = new AtomicDouble();
        bvy = new AtomicDouble();

        ly.set( 40f);
        ry.set( 40f);
        bx.set( 40f);
        by.set( 20f);
        bvx.set( 0.4f);
        bvy.set(0.4f);
        heartBeat  = new PongHeartBeat(this);
        heartBeat.start();
    }

    public List<PongPlayer> getPongPlayers() {
        return pongPlayers;
    }

    private PongPlayer findPlayer(String deviceHash) {
        Optional<PongPlayer> foundPlayer = pongPlayers.stream().filter(p-> p.getDevice().equalsIgnoreCase(deviceHash)).findFirst();
        if (foundPlayer.isPresent()) {
            return foundPlayer.get();
        }
        return null;
    }

    public String addPlayer(String deviceHash, String playerName) {
        PongPlayer pongPlayer = findPlayer(deviceHash);
        if (pongPlayer==null) {
            pongPlayer = new PongPlayer();
            pongPlayer.setDevice(deviceHash);
            pongPlayer.setName(playerName);
            pongPlayers.add(pongPlayer);
        } else {
            pongPlayer.setName(playerName);
        }
        version++;
        return "OK";
    }

    public int getVersion() {
        return version;
    }

    public String callPlayer(String caller, String called) {
        PongPlayer callerPlayer = findPlayer(caller);
        PongPlayer calledPlayer = findPlayer(called);
        callerPlayer.addCalled(called);
        calledPlayer.addCalledBy(caller);
        version++;

        return "OK";
    }

    public Integer getTiming() {
        return 100; // TODO later it could be set
    }

    public void startGame() {

    }

    public String getPos() {
        return "["+ly.get()+","+ry.get()+","+bx.get()+","+by.get()+"]";
    }

    public void step() {
        bx.set(bx.get()+bvx.get());
        by.set(by.get()+bvy.get());

        if (by.get()<2.0) {
            bvy.set(-bvy.get());
        } else {
            if (by.get() > 95.0) {
                bvy.set(-bvy.get());
            }
        }

        if (bx.get()<2.0) {
            bvx.set(-bvx.get());
        } else {
            if (bx.get()>95.0) {
                bvx.set(-bvx.get());
            }
        }

    }
}
