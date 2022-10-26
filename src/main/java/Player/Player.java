package Player;


import java.io.IOException;
import java.net.*;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

public class Player {
    private final Map<Byte, String> cards;
    private final Map<Integer, String> dice;
    private DatagramSocket send;
    private DatagramSocket receive;
    private int fc;
    private final int id;
    private final int port;
    private ArrayList<Integer> scoreCard;
    public Player(int id){
        fc = 0;
        cards = new HashMap<>();
        cards.put((byte) 0, "Treasure Chest");
        cards.put((byte) 1, "Captain");
        cards.put((byte) 2, "Sorceress");
        cards.put((byte) 3, "Sea Battle");
        cards.put((byte) 4, "Gold");
        cards.put((byte) 5, "Diamond");
        cards.put((byte) 6, "Monkey Business");
        cards.put((byte) 7, "Skulls");
        scoreCard = new ArrayList<>();
        dice = new HashMap<>();
        dice.put(0, "Coin");
        dice.put(1, "parrot");
        dice.put(2, "Monkey");
        dice.put(3, "Skull");
        dice.put(4,"Swords");
        dice.put(5, "Diamond");
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
        msg[0] = (byte)id;
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
