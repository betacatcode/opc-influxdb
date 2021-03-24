package com.xinda.xiaoxing.config.influxdb;

public class BatchProperties {
    private int actions;
    private int flushDuration;
    private int jitterDuration;
    private int bufferLimit;

    public int getActions() {
        return actions;
    }

    public void setActions(int actions) {
        this.actions = actions;
    }

    public int getFlushDuration() {
        return flushDuration;
    }

    public void setFlushDuration(int flushDuration) {
        this.flushDuration = flushDuration;
    }

    public int getJitterDuration() {
        return jitterDuration;
    }

    public void setJitterDuration(int jitterDuration) {
        this.jitterDuration = jitterDuration;
    }

    public int getBufferLimit() {
        return bufferLimit;
    }

    public void setBufferLimit(int bufferLimit) {
        this.bufferLimit = bufferLimit;
    }

    @Override
    public String toString() {
        return "BatchProperties{" +
                "actions=" + actions +
                ", flushDuration=" + flushDuration +
                ", jitterDuration=" + jitterDuration +
                ", bufferLimit=" + bufferLimit +
                '}';
    }
}
