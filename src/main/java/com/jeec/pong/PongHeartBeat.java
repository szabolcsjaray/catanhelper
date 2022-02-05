package com.jeec.pong;

public class PongHeartBeat extends Thread {

    PongGame myGame;
    boolean beating = true;

    public PongHeartBeat(PongGame game) {
        myGame = game;
    }

    public void stopBeat() {
        beating = false;
    }

    public void run() {
        do {
            myGame.step();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (beating);
    }

}
