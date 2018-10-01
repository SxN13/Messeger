package ru.sxn.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPCon {

    private final TCPL tcpl;
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPCon(TCPL tcpl, String oAddr, int oPort) throws IOException{
        this(tcpl, new Socket(oAddr, oPort));
    }

    public TCPCon(TCPL tcpl, Socket socket) throws IOException {

        this.tcpl = tcpl;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpl.onConectionReady(TCPCon.this);
                    while (!rxThread.isInterrupted()){
                        tcpl.onRecieve(TCPCon.this, in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    tcpl.onDisconnect(TCPCon.this);
                }
            }
        });
        rxThread.start();


    }

    public synchronized void sendMsg(String msg){
        try {
            out.write(msg + "\r\n" + ">");
            out.flush();
        } catch (IOException e) {
            tcpl.onException(TCPCon.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpl.onException(TCPCon.this,e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection " + socket.getInetAddress() + " " + socket.getPort();
    }
}
