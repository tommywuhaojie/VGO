package v_go.version10.SocketIo;


import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import v_go.version10.ApiClasses.Urls;

public class SocketIoHelper extends Application{

    private Socket mSocket;
    {
        try {
            //mSocket = IO.socket(Urls.TARGET_ROOT_URL);
            mSocket = IO.socket(Urls.TARGET_ROOT_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}

