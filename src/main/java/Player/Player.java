package Player;


import java.io.IOException;
import java.net.*;

public class Player {
    private DatagramSocket send;
    private DatagramSocket receive;
    private final Byte id;
    public Player(byte id, int port){
        this.id = id;
        try{
            send = new DatagramSocket();
            receive = new DatagramSocket(port, InetAddress.getLocalHost());
            System.out.println("Player "+ id + " has been initialize");
        }catch(IOException e){
            System.out.println(e);
        }
    }
    public void connect(int serverPort){
        byte[] msg = new byte[1];
        msg[0] = id;
        try {
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), serverPort);
            send.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
