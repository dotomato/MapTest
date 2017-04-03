package com.chen.maptest.Utils;

/**
 * Created by chen on 17-3-1.
 * Copyright *
 */


public abstract class OnceRunner implements Runnable {

    private static final int IDLE =-1;
    private static final int TAKE =0;
    private static final int NEEDRUN =1;
    private static final int FINISH =2;
    private static final int STOP=3;

    private int mStatue = IDLE;
    private int internal = 1000;

    @Override
    public void run() {
        while(true){

            switch (mStatue) {
                case NEEDRUN:
                    mStatue = TAKE;
                    break;
                case TAKE:
                    call();
                    mStatue = FINISH;
                    break;
                case FINISH:
                case IDLE:
                    break;
            }

            try {
                Thread.sleep(internal);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mStatue==STOP){
                break;
            }
        }
    }

    protected abstract void call();

    public void start() {
        this.mStatue = NEEDRUN;
    }

    public void stop(){
        this.mStatue = STOP;
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }
}
