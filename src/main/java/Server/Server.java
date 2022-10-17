package Server;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Server {
    private static DatagramSocket send;
    private static DatagramSocket receive;
    private static int port;

    public Server(int port){
        this.port = port;
        try{
            send = new DatagramSocket();
            System.out.println("Server is running at port: " + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            receive = new DatagramSocket(port, InetAddress.getLocalHost());
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        for(int i = 0; i< 3; i++){
            byte[] msg = new byte[1];
            DatagramPacket receivePacket = new DatagramPacket(msg, msg.length);
            try {
                receive.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Player " + receivePacket.getData()[0] +" has connected to server");
        }
    }


}
