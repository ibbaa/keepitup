package net.ibbaa.keepitup.test.mock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MockFuture<S> implements Future<S> {

    private boolean cancelled;
    private boolean done;

    public MockFuture() {
        reset();
    }

    public void reset() {
        cancelled = false;
        done = false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cancelled = true;
        return true;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public S get() throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public S get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return null;
    }
}
