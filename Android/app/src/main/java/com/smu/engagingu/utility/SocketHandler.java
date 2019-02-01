package com.smu.engagingu.utility;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketHandler{
    public Socket mSocket;
    public SocketHandler(){
        {
            try {
                mSocket = IO.socket("http://54.255.245.23:3000");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket(){
        return mSocket;
    }
}
