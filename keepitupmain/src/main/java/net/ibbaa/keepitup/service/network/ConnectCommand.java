package net.ibbaa.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.util.NumberUtil;

public class ConnectCommand implements Callable<ConnectCommandResult> {

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final int connectCount;
    private final ITimeService timeService;

    public ConnectCommand(Context context, InetAddress address, int port, int connectCount) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.connectCount = connectCount;
        this.timeService = createTimeService();
    }

    @Override
    public ConnectCommandResult call() {
        Log.d(ConnectCommand.class.getName(), "call");
        int attempts = 0;
        int successfulAttempts = 0;
        int timeouts = 0;
        int errors = 0;
        long overallTime = 0;
        Exception exception = null;
        for (int ii = 0; ii < connectCount; ii++) {
            Log.d(ConnectCommand.class.getName(), "Connection attempt " + (ii + 1));
            attempts++;
            try {
                ConnectionResult result = connect();
                if (result.isSuccess()) {
                    Log.d(ConnectCommand.class.getName(), "Connection was successful");
                    successfulAttempts++;
                    overallTime += result.getDuration();
                } else {
                    Log.d(ConnectCommand.class.getName(), "Connection timeout");
                    timeouts++;
                }
            } catch (Exception exc) {
                Log.e(ConnectCommand.class.getName(), "Connection error", exc);
                errors++;
                exception = exc;
            }
        }
        Log.d(ConnectCommand.class.getName(), "Connection attempts: " + attempts);
        Log.d(ConnectCommand.class.getName(), "Successful connection attempts: " + successfulAttempts);
        Log.d(ConnectCommand.class.getName(), "Timeouts:  " + timeouts);
        Log.d(ConnectCommand.class.getName(), "Overall time:  " + overallTime);
        double averageTime = successfulAttempts > 0 ? (double) overallTime / successfulAttempts : 0;
        Log.d(ConnectCommand.class.getName(), "Average time:  " + averageTime);
        Log.d(ConnectCommand.class.getName(), "Exception:  " + exception);
        return new ConnectCommandResult(successfulAttempts > 0, attempts, successfulAttempts, timeouts, errors, averageTime, exception);
    }

    protected ConnectionResult connect() throws IOException {
        Log.d(ConnectCommand.class.getName(), "connect");
        Socket socket = new Socket();
        long start = timeService.getCurrentTimestamp();
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, port);
            int timeout = getResources().getInteger(R.integer.connect_timeout);
            Log.d(ConnectCommand.class.getName(), "Connecting to " + sockaddr.toString());
            socket.connect(sockaddr, timeout * 1000);
            long end = timeService.getCurrentTimestamp();
            return new ConnectionResult(true, NumberUtil.ensurePositive(end - start));
        } catch (SocketTimeoutException exc) {
            Log.e(ConnectCommand.class.getName(), "Connection timeout", exc);
            long end = timeService.getCurrentTimestamp();
            return new ConnectionResult(false, NumberUtil.ensurePositive(end - start));
        } finally {
            try {
                Log.d(ConnectCommand.class.getName(), "Closing socket");
                socket.close();
            } catch (Exception exc) {
                Log.e(ConnectCommand.class.getName(), "Error closing socket", exc);
            }
        }
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    protected class ConnectionResult {
        private final boolean success;
        private final long duration;

        public ConnectionResult(boolean success, long duration) {
            this.success = success;
            this.duration = duration;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getDuration() {
            return duration;
        }
    }
}
