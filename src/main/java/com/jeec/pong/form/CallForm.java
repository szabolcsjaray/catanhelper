package com.jeec.pong.form;

public class CallForm {
    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCalled() {
        return called;
    }

    public void setCalled(String called) {
        this.called = called;
    }

    private String caller;
    private String called;

    @Override
    public String toString() {
        return "CallForm{" +
                "caller='" + caller + '\'' +
                ", called='" + called + '\'' +
                '}';
    }
}
