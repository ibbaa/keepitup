package de.ibba.keepitup.test.mock;

import de.ibba.keepitup.logging.IDump;
import de.ibba.keepitup.logging.IDumpSource;

public class MockDump implements IDump {

    private int dumpCalls;

    public MockDump() {
        reset();
    }

    @Override
    public void dump(IDumpSource source) {
        dumpCalls++;
    }

    public void reset() {
        dumpCalls = 0;
    }

    public boolean wasDumpCalled() {
        return numberDumpCalls() > 0;
    }

    public int numberDumpCalls() {
        return dumpCalls;
    }
}
