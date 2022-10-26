package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
    private final Map<Integer, String> fortuneCard;
    private int[] deck;
    private final Map<Integer, String> dice;
    private byte turn;
    private DatagramSocket send;
    private DatagramSocket receive;
    private final int port;
    private ArrayList<Integer> playerPort;
    private int fc;
    private ArrayList<Integer> rolled;
    private int[] score;

    public Server(int port, Boolean test){
        score = new int[3];
        rolled = new ArrayList<>();
        dice = new HashMap<>();
        dice.put(0, "Coin");
        dice.put(1, "parrot");
        dice.put(2, "Monkey");
        dice.put(3, "Skull");
        dice.put(4,"Swords");
        dice.put(5, "Diamond");
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

        turn = 0;
        deck = new int[]{4,4,4,2,2,2,4,4,4,3,2};

        this.port = port;
        try{
            send = new DatagramSocket();
            System.out.println("Server is running at port: " + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        playerPort = new ArrayList<>();

    }

    public void gameStart(){
        while(true){
            nextTurn();
            rolled.clear();
            //send player turn, first dice roll, and fortune card to everyone
            byte[] msg = new byte[11];
            msg[0] = getTurn();
            this.fc = drawCard();
            msg[1] = (byte) fc;
            for(int i = 0; i< 8; i++){
                rolled.add(rollDice());
            }
            if(isDead(rolled, fc)){
                msg[2] = 1;
            }else{
                msg[2] = 0;
            }
            for(int i = 2; i<10;i++){
                msg[i] = (byte)(int)rolled.get(i-2);
            }
            broadcast(msg);
            //wait for player respond(re-roll)
            while(msg[2]!=1){
                try{
                    //first byte = 0 means is don't re-roll, first byte = 1 means re-roll. second byte =1 means player's dead
                    // following bytes are the dice
                    //that player wants to re-roll
                    byte[] message = new byte[9];
                    DatagramPacket receivePacket = new DatagramPacket(message, message.length);
                    receive.receive(receivePacket);
                    if(receivePacket.getData()[0] == 0){
                        break;
                    }else{
                        ArrayList<Integer> index = new ArrayList<>();
                        for(int i = 1; i< 9; i++){
                            index.add((int) receivePacket.getData()[i]);
                        }
                        rolled = re_roll(rolled, index);
                        msg[0] = getTurn();
                        for(int i = 1; i< 9;i++){
                            msg[i] = (byte)(int)rolled.get(i-1);
                        }
                        broadcast(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //player decided not to re-roll or died
            int turnScore = countScore(rolled, fc);
            score[getTurn()]+=turnScore;

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

    //base value of dice, without skull->die rule and fortune cards rule
    public int calculateScore(int[] dice, int fc){
        int score = 0;
        //count numbers
        for(int i = 0; i< dice.length; i++){
            if(i == 3){
                continue;
            }
            switch (dice[i]){
                case 3:
                    score+=100;
                    break;
                case 4:
                    score+=200;
                    break;
                case 5:
                    score+=500;
                    break;
                case 6:
                    score+=1000;
                    break;
                case 7:
                    score+=2000;
                    break;
                case 8:
                case 9:
                    score+=4000;
                    break;
            }
        }
        //count coins and diamonds
        score += (dice[0]+dice[5])*100;
        //full chest
        boolean fullChest = true;
        if(fc == 4){
            dice[0]--;
        }
        if(fc == 5){
            dice[5]--;
        }
        for(int die: dice){
            if(die <=3 && die != 0){
                fullChest = false;
                break;
            }
        }
        if(fullChest){
            score += 500;
        }
        return score;
    }

    public int[] countDice(ArrayList<Integer> dice){
        int[] list = new int[6];
        for(int i = 0; i< 6; i++){
            list[i] = 0;
        }
        for(int die: dice){
            list[die]++;
        }
        return list;
    }

    public int countScore(ArrayList<Integer> dice, int fc) {
        int score = 0;

        if (isDead(dice, fc)) {
            return 0;
        } else {
            int[] list = countDice(dice);
            switch (fc) {
                case 0://treasure chest
                case 2://Sorceress
                case 72://2 skulls
                case 71://1 skull
                    score = calculateScore(list, 0);
                    break;
                case 1://captain
                    score = 2 * calculateScore(list, 1);
                    break;
                case 34://sea battle with 4 swords, 1000 points
                    score = calculateScore(list, 34);
                    if (list[4] == 4) {
                        score += 1000;
                    } else {
                        score -= 1000;
                    }
                    break;
                case 32://sea battle with 2 swords, 300 points
                    score = calculateScore(list, 32);
                    if (list[4] == 2) {
                        score += 300;
                    } else {
                        score -= 300;
                    }
                    break;
                case 33://sea battle with 3 swords, 500 points
                    score = calculateScore(list, 33);
                    if (list[4] == 3) {
                        score += 500;
                    } else {
                        score -= 500;
                    }
                    break;
                case 4://gold
                    list[0] += 1;
                    score = calculateScore(list, 4);
                    break;
                case 5://diamond
                    list[5] += 1;
                    score = calculateScore(list, 5);
                    break;
                case 6://Monkey business
                    list[2] += list[3];
                    list[3] = 0;
                    score = calculateScore(list, 6);
                    break;
            }

            return score;
        }
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
            if(skulls >=3){
                System.out.println("Player " + getTurn() + " is dead(" + skulls+" skulls)");
                return true;
            }else{
                return false;
            }
        }
    }

    //re-roll with cheat, outcome can be specified
    public ArrayList<Integer> re_roll(ArrayList<Integer> dice, ArrayList<Integer> index, ArrayList<Integer> outcome){
        ArrayList<String> reroll = new ArrayList<>();
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
        }
        System.out.print("Player "+ getTurn() + " re-rolling dice: " + reroll);
        for(int i: index){
            dice.set(i, -1);
        }
        ArrayList<String> resultString = new ArrayList<>();
        for(int i: outcome){
            resultString.add(this.dice.get(i));
        }
        System.out.println(", got "+resultString);
        for(int i: index){
            dice.remove((Integer) (-1));

        }
        dice.addAll(outcome);
        return dice;
    }

    public int rollDice(){
        Random random = new Random();
        int num;
        num = random.nextInt(6);
        return num;
    }

    //player can re-roll a number of dice
    public ArrayList<Integer> re_roll(ArrayList<Integer> dice, ArrayList<Integer> index){
        ArrayList<String> reroll = new ArrayList<>();
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
        }
        System.out.print("Player "+ getTurn() + " re-rolling dice: " + reroll);
        for(int i: index){
            dice.set(i, -1);
        }
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < index.size(); i++){
            result.add(rollDice());
        }
        ArrayList<String> resultString = new ArrayList<>();
        for(int i: result){
            resultString.add(this.dice.get(i));
        }
        System.out.println(", got "+resultString);
        for(int i: index){
            dice.remove((Integer) (-1));

        }
        dice.addAll(result);
        return dice;
    }


    public int drawCard(){
        Random random = new Random();
        byte card = (byte)random.nextInt(11);
        if(this.deck[card] == 0){
            drawCard();
        }else{
            this.deck[card]--;
            System.out.println("Player " + getTurn() + " draw fortune card " + fortuneCard.get(card));
            return card;
        }
        return 0;
    }

    public void broadcast(byte[] msg){
        try{
            for(int p: playerPort){
                DatagramPacket sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), p);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}
