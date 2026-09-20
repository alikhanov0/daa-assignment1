package com.alikhanov;

public final class Metrics {
    private long comparisons;
    private int maxDepth;
    private long startedAt;
    private long elapsedNanos;

    public void comparison() {
        comparisons++;
    }

    public long comparisons() {
        return comparisons;
    }

    public void recordDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public int maxDepth() {
        return maxDepth;
    }

    public void startTimer() {
        startedAt = System.nanoTime();
    }

    public void stopTimer() {
        elapsedNanos = System.nanoTime() - startedAt;
    }

    public double timeMillis() {
        return elapsedNanos / 1_000_000.0;
    }
}
