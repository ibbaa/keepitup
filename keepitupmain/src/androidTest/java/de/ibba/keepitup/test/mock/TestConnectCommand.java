package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import de.ibba.keepitup.service.network.ConnectCommand;

public class TestConnectCommand extends ConnectCommand {

    private List<MockConnectionResult> connectionResults;
    private int call;

    public TestConnectCommand(Context context, InetAddress address, int port, int connectCount) {
        super(context, address, port, connectCount);
        reset();
    }

    public void reset() {
        call = 0;
    }

    public void setConnectionResults(List<MockConnectionResult> connectionResults) {
        this.connectionResults = connectionResults;
    }

    @Override
    protected ConnectionResult connect() throws IOException {
        if (connectionResults != null && call < connectionResults.size()) {
            MockConnectionResult result = connectionResults.get(call);
            call++;
            IOException exception = result.getException();
            if (exception != null) {
                throw exception;
            }
            return result;
        }
        return null;
    }

    public class MockConnectionResult extends ConnectionResult {
        private final IOException exception;

        public MockConnectionResult(boolean success, long duration, IOException exception) {
            super(success, duration);
            this.exception = exception;
        }

        public IOException getException() {
            return exception;
        }
    }
}
