package Player;


import java.io.IOException;
import java.net.*;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

public class Player {
    private final Map<Integer, String> fortuneCard;
    private final Map<Integer, String> dice;
    private DatagramSocket send;
    private DatagramSocket receive;
    private int fc;
    private final int id;
    private final int port;
    private ArrayList<Integer> scoreCard;
    public Player(int id){
        fc = 0;
        fortuneCard = new HashMap<>();
        fortuneCard.put(1, "Treasure Chest");
        fortuneCard.put(2, "Captain");
        fortuneCard.put(3, "Sorceress");
        fortuneCard.put(42, "Sea Battle(2 swords, 300");
        fortuneCard.put(43, "Sea Battle(3 swords, 500");
        fortuneCard.put(44, "Sea Battle(4 swords, 1000");
        fortuneCard.put(5, "Gold");
        fortuneCard.put(6, "Diamond");
        fortuneCard.put(7, "Monkey Business");
        fortuneCard.put(81, "One Skull");
        fortuneCard.put(82, "Two Skulls");
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
    public void connect(){
        byte[] msg = new byte[1];
        msg[0] = (byte)id;
        sendToServer(msg);
        byte[] response = receiveMessage(1);
        if(response[0] == 1){
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

    public void play(){
        while(true){
            byte[] msg = receiveMessage(12);
            boolean yourTurn = (msg[0] == this.id);
            if(yourTurn){
                //player's turn
                System.out.println("it is your turn to play");
                System.out.println("Fortune card: " + fortuneCard.get((int)msg[1]));
                ArrayList<String> dice = new ArrayList<>();
                for(int i = 3; i<11; i++){
                    dice.add(this.dice.get((int)msg[i]));
                }
                System.out.println("You rolled: "+ dice);

                if(msg[2] == 1) {
                    System.out.println("You are dead");
                }else {
                    //player is not dead, ask if player want to re-roll
                    System.out.println("Do you wish to Re-roll our dice?");
                    System.out.println("1) Yes    2) No");
                    Scanner input = new Scanner(System.in);
                    byte[] choice = new byte[1];
                    choice[0] = input.nextByte();
                    input.nextLine();//consume \n
                    sendToServer(choice);
                    receiveMessage(1);
                    while(choice[0] == 1){
                        //player choose to re-roll
                        System.out.println("Please input the index of the dice you want to re-roll(separated by space: ");
                        for(int i = 0; i< 8; i++){
                            System.out.printf("%10s", dice.get(i));
                        }
                        System.out.println();
                        for(int i = 0; i<8;i++){
                            System.out.printf("%7d", i);
                            System.out.print("   ");
                        }
                        System.out.println();
                        //format indices of dice to re-roll and send to server
                        String[] index = input.nextLine().split(" ");
                        byte[] reroll = new byte[8];
                        for(int i = 0; i< index.length; i++){
                            if(!index[i].equals("")){
                                reroll[i] = (byte) Integer.parseInt(index[i]);
                            }
                        }
                        for(int i = index.length; i < 8; i++){
                            reroll[i] = -1;
                        }
                        System.out.println();
                        sendToServer(reroll);
                        byte[] rerolled = receiveMessage(8);
                        dice.clear();
                        for(int i =0;i<8;i++){
                            dice.add(this.dice.get((int)rerolled[i]));
                        }
                        System.out.println("Your dice now is: " + dice);
                        byte[] dead = receiveMessage(1);
                        if(dead[0] == 1){
                            System.out.println("You died.");
                            break;
                        }
                        System.out.println("Do you wish to re-roll again?");
                        System.out.println("1) Yes    2) No");
                        choice[0] = input.nextByte();
                        input.nextLine();
                        sendToServer(choice);
                        receiveMessage(1);
                    }
                }
            }else{
                //not player's turn
                System.out.println("it is player "+ msg[0]+" turn to play");
                System.out.println("Fortune card: " + fortuneCard.get((int)msg[1]));
                ArrayList<String> dice = new ArrayList<>();
                for(int i = 3; i<11; i++){
                    dice.add(this.dice.get((int)msg[i]));
                }
                System.out.println("Player" +msg[0] +" rolled: " + dice);
                if(msg[2] == 1){
                    //player is dead
                    System.out.println("Player "+msg[0]+" is dead");
                }else{
                    //player is not dead
                    byte[] choice = receiveMessage(1);
                    while(choice[0] == 1){
                        //player choose not to re-roll
                        System.out.println("Player " + msg[0] +" is re-rolling");
                        byte[] rerolled = receiveMessage(8);
                        dice.clear();
                        for(byte die: rerolled){
                            dice.add(this.dice.get((int) die));
                        }
                        System.out.println("Player "+ msg[0] + " re-rolled: " + dice);
                        byte[] dead = receiveMessage(1);
                        if(dead[0] == 1){
                            System.out.println("Player " + msg[0] +" is dead");
                            break;
                        }
                        choice = receiveMessage(1);
                    }
                }
            }
            //calculate score

        }
    }

    public byte[] receiveMessage(int length){
        try{
            byte[] msg = new byte[length];
            DatagramPacket receivePacket = new DatagramPacket(msg, length);
            receive.receive(receivePacket);

            return receivePacket.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendToServer(byte[] msg){
        try {
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 4000);
            send.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        receive.close();
    }
}
