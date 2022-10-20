package Player;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player {
    private Map<Byte, String> cards;
    private Map<Integer, String> dice;
    private DatagramSocket send;
    private DatagramSocket receive;
    private final Byte id;
    private int port;
    private ArrayList<Integer> scoreCard;
    public Player(byte id){
        cards = new HashMap<>();
        cards.put((byte) 0,"Treasure Chest");
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
        dice.put(1, "Fish");
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

    public String drawCard(){
        Random random = new Random();
        byte card = (byte)random.nextInt(8);
        System.out.println(card);
        return cards.get(card);
    }

    public ArrayList<String> rollDice(){
        ArrayList<String> result = new ArrayList<>();
        Random random = new Random();
        int num;
        for(int i = 0; i < 8; i++){
            num = random.nextInt(6);
            result.add(dice.get(num));
        }
        return result;
    }


}
