package com.android.pfg7001;

import java.net.Socket;

public class SocketHandler {

    private static Socket socket1;
    private static Socket socket2;
    private static Socket socket3;
    private static Socket socket4;

    public static synchronized Socket getSocket1(){
        return socket1;
    }

    public static synchronized Socket getSocket2(){
        return socket2;
    }

    public static synchronized Socket getSocket3(){
        return socket3;
    }

    public static synchronized Socket getSocket4(){
        return socket4;
    }

    public static synchronized void setSocket1(Socket socket){
        SocketHandler.socket1 = socket;
    }

    public static synchronized void setSocket2(Socket socket){
        SocketHandler.socket2 = socket;
    }

    public static synchronized void setSocket3(Socket socket){
        SocketHandler.socket3 = socket;
    }

    public static synchronized void setSocket4(Socket socket){
        SocketHandler.socket4 = socket;
    }

}
