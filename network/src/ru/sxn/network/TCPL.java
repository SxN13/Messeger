package ru.sxn.network;

public interface TCPL {

    void onConectionReady(TCPCon tcpCon);
    void onDisconnect(TCPCon tcpCon);
    void onRecieve(TCPCon tcpCon, String str);
    void onException(TCPCon tcpCon, Exception exc);

}