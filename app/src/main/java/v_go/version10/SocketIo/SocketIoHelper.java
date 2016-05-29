package v_go.version10.SocketIo;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIoHelper extends Application {

    private Socket mSocket;
    {
        try {
            final String SERVER_URL = "http://chat.socket.io";
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}

