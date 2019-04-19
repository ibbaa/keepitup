package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.ui.sync.IHandler;

public class MockHandler implements IHandler {

    @Override
    public boolean start(Runnable runnable) {
        return false;
    }

    @Override
    public boolean startDelayed(Runnable runnable, long delay) {
        return false;
    }

    @Override
    public void stop(Runnable runnable) {

    }
}
