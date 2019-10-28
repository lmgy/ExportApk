package com.lmgy.exportapk.event;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class ProgressEvent {

    private int progress;

    public ProgressEvent(int progress){
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
