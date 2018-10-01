package ru.sxn.server;

import ru.sxn.network.TCPCon;
import ru.sxn.network.TCPL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerC implements TCPL{

    public static void main(String[] args) {

        new ServerC();

    }

    private final ArrayList<TCPCon> connections = new ArrayList<>();

    private ServerC(){

        System.out.println("SRun");
        try (ServerSocket serverSocket = new ServerSocket(1408)){
            while (true){
                try {
                    new TCPCon(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void onConectionReady(TCPCon tcpCon) {
        connections.add(tcpCon);
        sendMessAll("Connected " + tcpCon);
    }

    @Override
    public synchronized void onDisconnect(TCPCon tcpCon) {
        connections.remove(tcpCon);
        sendMessAll("Disconnect " + tcpCon);
    }

    @Override
    public synchronized void onRecieve(TCPCon tcpCon, String str) {
        sendMessAll(tcpCon + ": " + str);
    }

    @Override
    public synchronized void onException(TCPCon tcpCon, Exception exc) {
        System.out.println(exc);
    }

    private void sendMessAll(String msg){
        System.out.println(msg);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            connections.get(i).sendMsg(msg);
        }
    }
}
