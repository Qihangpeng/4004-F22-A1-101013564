package Player;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Player {
    private DatagramSocket send;
    private DatagramSocket receive;
    private final Byte id;
    private int port;
    private ArrayList<Integer> scoreCard;
    public Player(byte id){
        scoreCard = new ArrayList<>();
        this.port = id + 4000;
        this.id = id;
        try{
            send = new DatagramSocket();
            receive = new DatagramSocket(this.port, InetAddress.getLocalHost());
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
            DatagramPacket receivePacket = new DatagramPacket(msg, msg.length);
            receive.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(msg[0] == 1){
            System.out.println("Player " + id + " received response from server.");
        }
    }

    public int score(){
        System.out.println(scoreCard);
        int sum = 0;
        for(int num: scoreCard){
            sum+=num;
        }
        return sum;
    }


}
