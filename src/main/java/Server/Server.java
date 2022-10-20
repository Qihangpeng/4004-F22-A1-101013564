package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private Map<String, Integer> fortuneCard;
    private byte turn;
    private DatagramSocket send;
    private DatagramSocket receive;
    private int port;
    private ArrayList<Integer> playerPort;

    public Server(int port){
        fortuneCard = new HashMap<>();
        fortuneCard.put("Treasure Chest", 1);
        fortuneCard.put("Captain", 2);
        fortuneCard.put("Sorceress", 3);
        fortuneCard.put("Sea Battle", 4);
        fortuneCard.put("Gold", 5);
        fortuneCard.put("Diamond", 6);
        fortuneCard.put("Monkey Business", 7);
        fortuneCard.put("Skulls", 8);
        turn = 1;
        this.port = port;
        try{
            send = new DatagramSocket();
            System.out.println("Server is running at port: " + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        playerPort = new ArrayList<>();
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
            System.out.println("Player " + receivePacket.getData()[0] +" (port: "+ (receivePacket.getData()[0]+4000) +")  has connected to server");
            playerPort.add((int)receivePacket.getData()[0] + port);
        }
        System.out.println(playerPort);
    }


    public void start(){
        for(int port: playerPort){
            byte[] msg = new byte[1];
            msg[0] = 1;
            try{
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), port);
                send.send(sendPacket);
            }catch(IOException e){
                System.out.println(e);
            }
        }
    }

    public byte nextTurn(){
        turn++;
        if(turn == 4){
            turn = 1;
        }
        return turn;
    }

    public byte getTurn(){
        return turn;
    }

    public void setTurn(int turn){
        this.turn = (byte)turn;
    }


}
