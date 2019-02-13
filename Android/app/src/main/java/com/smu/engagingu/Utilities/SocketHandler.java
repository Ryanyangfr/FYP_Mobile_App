package com.smu.engagingu.Utilities;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketHandler{
    public Socket mSocket;
    public SocketHandler(){
        {
            try {
                mSocket = IO.socket("http://13.229.115.32:3000");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket(){
        return mSocket;
    }
}
