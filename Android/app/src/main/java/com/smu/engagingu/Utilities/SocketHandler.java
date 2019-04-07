package com.smu.engagingu.Utilities;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
/*
 * SocketHandler is used to initialise the socket used to communicate between the mobile application
 * and the AWS back-end in real time
 */
public class SocketHandler{
    public Socket mSocket;
    public SocketHandler(){
        {
            try {
                mSocket = IO.socket("http://13.228.173.165:3000");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public Socket getSocket(){
        return mSocket;
    }
}
