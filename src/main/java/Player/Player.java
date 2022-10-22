package Player;


import java.io.IOException;
import java.net.*;
import java.util.*;

public class Player {
    private Map<Byte, String> cards;
    private Map<Integer, String> dice;
    private DatagramSocket send;
    private DatagramSocket receive;
    private final int id;
    private int port;
    private ArrayList<Integer> scoreCard;
    public Player(int id){
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

    public String drawCard(){
        Random random = new Random();
        byte card = (byte)random.nextInt(8);
        System.out.println(card);
        return cards.get(card);
    }

    public int rollDice(){
        Random random = new Random();
        int num;
        num = random.nextInt(6);
        return num;
    }

    //player can re-roll a number of dice
    public ArrayList<Integer> re_roll(ArrayList<Integer> dice, ArrayList<Integer> index){
        for(int i = 0; i < index.size(); i++){
            dice.set(i, rollDice());
        }
        return dice;
    }

    //re-roll with cheat, outcome can be specified
    public ArrayList<Integer> re_roll(ArrayList<Integer> dice, ArrayList<Integer> index, ArrayList<Integer> outcome){
        for(Integer i: index){
            dice.remove(i);
        }
        dice.addAll(outcome);
        return dice;
    }

    public int countScore(ArrayList<Integer> dice){
        int score = 0;
        for(int i: dice){

        }
        return score;
    }

    public Boolean isDead(ArrayList<Integer> dice, int fc){
        if(fc == 7){
            return false;
        }else{
            int skulls = 0;
            for(Integer die: dice){
                if(die == 3){
                    skulls++;
                }
            }
            return skulls >= 2;
        }
    }


}
