package de.ibba.keepitup.test.mock;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.ui.sync.IHandler;

public class MockHandler implements IHandler {

    private final List<StartCall> startCalls;
    private final List<StartDelayedCall> startDelayedCalls;
    private final List<StopCall> stopCalls;

    private boolean doCall;

    public MockHandler() {
        startCalls = new ArrayList<>();
        startDelayedCalls = new ArrayList<>();
        stopCalls = new ArrayList<>();
        doCall = true;
    }

    public List<StartCall> getStartCalls() {
        return startCalls;
    }

    public List<StartDelayedCall> getStartDelayedCalls() {
        return startDelayedCalls;
    }

    public List<StopCall> getStopCalls() {
        return stopCalls;
    }

    public void setDoCall(boolean doCall) {
        this.doCall = doCall;
    }

    public void reset() {
        startCalls.clear();
        startDelayedCalls.clear();
        stopCalls.clear();
    }

    public boolean wasStartCalled() {
        return !startCalls.isEmpty();
    }

    public boolean wasStartDelayedCalled() {
        return !startDelayedCalls.isEmpty();
    }

    public boolean wasStopCalled() {
        return !stopCalls.isEmpty();
    }

    @Override
    public boolean start(Runnable runnable) {
        startCalls.add(new StartCall(runnable));
        if (doCall) {
            runnable.run();
        }
        return true;
    }

    @Override
    public boolean startDelayed(Runnable runnable, long delay) {
        startDelayedCalls.add(new StartDelayedCall(runnable, delay));
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stopCalls.add(new StopCall(runnable));
    }

    public static class StartCall {

        private final Runnable runnable;

        public StartCall(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }

    public static class StartDelayedCall {

        private final Runnable runnable;
        private final long delay;

        public StartDelayedCall(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delay = delay;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public long getDelay() {
            return delay;
        }
    }

    public static class StopCall {

        private final Runnable runnable;

        public StopCall(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }
}
