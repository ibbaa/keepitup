package de.ibba.keepitup.test.mock;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.logging.IDump;
import de.ibba.keepitup.logging.IDumpSource;

public class MockDump implements IDump {

    private final List<DumpCall> dumpCalls;

    public MockDump() {
        dumpCalls = new ArrayList<>();
    }

    @Override
    public void dump(String tag, String message, IDumpSource source) {
        dumpCalls.add(new DumpCall(tag, message, source));
    }

    public void reset() {
        dumpCalls.clear();
    }

    public boolean wasDumpCalled() {
        return numberDumpCalls() > 0;
    }

    public int numberDumpCalls() {
        return dumpCalls.size();
    }

    public DumpCall getDumpCall(int index) {
        return dumpCalls.get(index);
    }

    public class DumpCall {

        private final String tag;
        private final String message;
        private final IDumpSource dumpSource;

        public DumpCall(String tag, String message, IDumpSource dumpSource) {
            this.tag = tag;
            this.message = message;
            this.dumpSource = dumpSource;
        }

        public String getTag() {
            return tag;
        }

        public String getMessage() {
            return message;
        }

        public IDumpSource getDumpSource() {
            return dumpSource;
        }
    }
}
