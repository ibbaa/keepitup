package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

import de.ibba.keepitup.R;

public class ConnectCommand implements Callable<ConnectCommandResult> {

    private final Context context;
    private final InetAddress address;
    private final int port;

    public ConnectCommand(Context context, InetAddress address, int port) {
        this.context = context;
        this.address = address;
        this.port = port;
    }

    @Override
    public ConnectCommandResult call() {
        Log.d(ConnectCommand.class.getName(), "call");
        Socket socket = new Socket();
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, port);
            int timeout = getResources().getInteger(R.integer.connect_timeout);
            Log.d(ConnectCommand.class.getName(), "Connecting to " + sockaddr.toString());
            socket.connect(sockaddr, timeout * 1000);
            return new ConnectCommandResult(true, null);
        } catch (Exception exc) {
            Log.e(ConnectCommand.class.getName(), "Error executing connect command", exc);
            return new ConnectCommandResult(false, exc);
        } finally {
            try {
                socket.close();
            } catch (Exception exc) {
                Log.e(ConnectCommand.class.getName(), "Error closing socket", exc);
            }
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
