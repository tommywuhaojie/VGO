package v_go.version10.SocketIo;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import v_go.version10.ApiClasses.ServerUrls;

public class SocketIoHelper{

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ServerUrls.HOST_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}

