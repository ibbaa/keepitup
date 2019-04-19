package de.ibba.keepitup.ui.sync;

public interface IHandler {

    boolean start(Runnable runnable);

    boolean startDelayed(Runnable runnable, long delay);

    void stop(Runnable runnable);
}
