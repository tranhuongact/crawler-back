package crawl.demo.mycrawler.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {

    private DatagramSocket socket = null;
    private String SERVER_IP = "127.0.0.1";
    private int SERVER_PORT = 7;
    private byte[] buf = new byte[4096];
    private InetAddress server;

    public Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        server = InetAddress.getByName(SERVER_IP);
    }

    public void run(String msg) {
        try {
            buf = msg.getBytes();

            // Tạo gói tin gởi
            DatagramPacket packet = new DatagramPacket(buf, buf.length, server, SERVER_PORT);
            socket.send(packet);

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}