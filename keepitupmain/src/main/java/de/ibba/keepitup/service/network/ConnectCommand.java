package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.service.ITimeService;

public class ConnectCommand implements Callable<ConnectCommandResult> {

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final ITimeService timeService;

    public ConnectCommand(Context context, InetAddress address, int port) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.timeService = createTimeService();
    }

    @Override
    public ConnectCommandResult call() {
        Log.d(ConnectCommand.class.getName(), "call");
        Socket socket = new Socket();
        long start = timeService.getCurrentTimestamp();
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, port);
            int timeout = getResources().getInteger(R.integer.connect_timeout);
            Log.d(ConnectCommand.class.getName(), "Connecting to " + sockaddr.toString());
            socket.connect(sockaddr, timeout * 1000);
            long end = timeService.getCurrentTimestamp();
            return new ConnectCommandResult(true, end - start, null);
        } catch (Exception exc) {
            Log.e(ConnectCommand.class.getName(), "Error executing connect command", exc);
            long end = timeService.getCurrentTimestamp();
            return new ConnectCommandResult(false, end - start, exc);
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

    public ITimeService getTimeService() {
        return timeService;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
