package de.ibba.keepitup.test.mock;

import java.io.IOException;
import java.io.InputStream;

public class BlockingTestInputStream extends InputStream {

    private final Signal signal;

    public BlockingTestInputStream(Signal signal) {
        this.signal = signal;
    }

    @Override
    public int read() throws IOException {
        if (signal.doContinue()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                //do nothing
            }
        }
        return 1;
    }

    @FunctionalInterface
    public interface Signal {
        boolean doContinue();
    }
}
