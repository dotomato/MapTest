package com.chen.maptest.Utils;

/**
 * Created by chen on 17-3-1.
 * Copyright *
 */


public abstract class OnceRunner implements Runnable {

    private OnceRunner mInternalRunner;
    private int mStatue;
    private static final int FINISH=0;
    private static final int RUN=1;
    private static final int STOP=2;

    private int internal = 1000;

    @Override
    public void run() {
        while(true){
            if (mStatue==RUN) {
                call();
                mStatue = FINISH;
            }
            if (mStatue==STOP){
                break;
            }
            try {
                Thread.sleep(internal);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void call();

    public void start() {
        this.mStatue = RUN;
    }

    public void stop(){
        this.mStatue = STOP;
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }
}
