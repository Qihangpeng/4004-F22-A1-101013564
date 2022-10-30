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
    private boolean canReroll;
    private int countDown;

    public Server(int port, Boolean test){
        countDown = 3;
        canReroll = true;
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
        fortuneCard.put(0, "Treasure Chest");
        fortuneCard.put(1, "Captain");
        fortuneCard.put(2, "Sorceress");
        fortuneCard.put(32, "Sea Battle(2 swords, 300");
        fortuneCard.put(33, "Sea Battle(3 swords, 500");
        fortuneCard.put(34, "Sea Battle(4 swords, 1000");
        fortuneCard.put(4, "Gold");
        fortuneCard.put(5, "Diamond");
        fortuneCard.put(6, "Monkey Business");
        fortuneCard.put(71, "One Skull");
        fortuneCard.put(72, "Two Skulls");

        turn = 0;
        deck = new int[]{4,4,4,2,2,2,4,4,4,3,2};

        this.port = port;
        try{
            send = new DatagramSocket();
            receive = new DatagramSocket(port, InetAddress.getLocalHost());
            System.out.println("Server is running at port: " + port);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        playerPort = new ArrayList<>();

    }

    public void gameStart(boolean cheat, ArrayList<ArrayList<Integer>> command){
        System.out.println(command);

        while(true){
            nextTurn();
            rolled.clear();
            //send player turn, first dice roll, and fortune card to everyone
            byte[] msg = new byte[11];
            msg[0] = getTurn();
            //if cheat, draw card, else, use the card specified
            if(cheat){
                this.fc = command.get(0).get(0);
                command = nextCommand(command);
            }else{
                this.fc = drawCard();
            }
            msg[1] = (byte) fc;

            //determine dice depending on cheat
            if(cheat){
                rolled = command.get(0);
                command = nextCommand(command);
            }else{
                for(int i = 0; i< 8; i++){
                    rolled.add(rollDice());
                }
            }
            //display player roll
            System.out.print("Player rolled: ");
            for(int i =0; i<8;i++){
                System.out.printf("%10s", this.dice.get(rolled.get(i)));
            }
            System.out.println();
            if(isDead(rolled, fc)){
                msg[2] = 1;
            }else{
                msg[2] = 0;
            }
            for(int i = 3; i<11;i++){
                msg[i] = (byte)(int)rolled.get(i-3);
            }
            broadcast(msg);
            if(msg[2] == 1){
                System.out.println("Player "+ this.turn+ " is dead");
                int remaining = sendScore();
                byte[] remainingByte = new byte[1];
                remainingByte[0] = (byte)remaining;
                broadcast(remainingByte);
                if(remaining == 0){
                    break;
                }
                continue;
            }
            //wait for player respond(re-roll)
            byte[] choice = receive(1);
            broadcast(choice);
            while(choice[0]!=2){
                //receive 9 bytes that indicate which dice to re-roll
                System.out.println("Player "+ this.turn+" re-rolling");
                byte[] message = receive(8);

                ArrayList<Integer> index = new ArrayList<>();
                for(int i = 0; i< 8; i++){
                    if(message[i] >=0) {
                        index.add((int) message[i]);
                    }
                }
                rolled = re_roll(rolled, index);
                for(int i = 0; i< 8;i++){
                    msg[i] = (byte)(int)rolled.get(i);
                }
                //send new result to everyone
                broadcast(msg);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //check if player is dead with new result
                byte[] dead = new byte[1];
                if(isDead(rolled, fc)){
                    dead[0] = 1;
                }
                broadcast(dead);
                if(dead[0] == 1){
                    break;
                }
                //determine if player can re-roll
                byte[] can = new byte[1];

                if(canReroll){
                    can[0] = 1;
                    broadcast(can);
                }else{
                    broadcast(can);
                    break;
                }

                //wait for user to decide if they want to re-roll again
                choice = receive(1);
                broadcast(choice);
            }
            int remaining = sendScore();
            if(remaining == 0){
                byte[] remainingByte = new byte[1];
                remainingByte[0] = (byte)remaining;
                broadcast(remainingByte);
                break;
            }
        }
        int winner = 1;
        for(int i = 0; i<3; i++){
            if (score[i] > score[winner-1]){
                winner = i+1;
            }
        }
        System.out.println("Player "+ winner+ " won the game");
    }

    public void connect(){
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
        byte[] msg = new byte[1];
        msg[0] = 1;
        broadcast(msg);
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
        for(int i = 0; i<6; i++){
            if(i == 0 || i == 5){
                continue;
            }
            if(i == 4){
                if(dice[i] >=2){
                    continue;
                }
            }
            if(dice[i] < 3 && dice[i] != 0){
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
            switch(fc){
                case 32:
                    score = -300;
                    break;
                case 33:
                    score = -500;
                    break;
                case 34:
                    score = -1000;
                    break;
            }

        } else {
            int[] list = countDice(dice);
            //counting island of dead
            int skulls = list[3];

            //normal score
            switch (fc) {
                case 0://treasure chest
                case 8://used sorceress
                case 2://Sorceress
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
                        score = -1000;
                    }
                    break;
                case 32://sea battle with 2 swords, 300 points
                    score = calculateScore(list, 32);
                    if (list[4] == 2) {
                        score += 300;
                    } else {
                        score = -300;
                    }
                    break;
                case 33://sea battle with 3 swords, 500 points
                    score = calculateScore(list, 33);
                    if (list[4] == 3) {
                        score += 500;
                    } else {
                        score = -500;
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
                    list[1] += list[2];
                    list[2] = 0;
                    score = calculateScore(list, 6);
                    break;
                case 72://2 skulls
                    score = calculateScore(list, 72);
                    skulls+=2;
                    break;
                case 71://1 skull
                    score = calculateScore(list, 71);
                    skulls++;
                    break;

            }
            //island of dead, cannot enter island of dead if fc is swords
            if(skulls >=4 && fc != 32 && fc != 33 && fc != 34 ){
                int reduce = skulls * -100;
                if(fc == 1){
                    reduce*=2;
                }
                System.out.println("Player entered island of the dead("+skulls+" skulls), other players reduce "+reduce+" points");
                for(int i = 0; i<3; i++){
                    if(i != this.turn){
                        this.score[i]+=reduce;
                    }
                    if(this.score[i] <0){
                        this.score[i] = 0;
                    }
                }
                score = 0;
            }
        }
        System.out.println("Player scored "+ score +" this round.");

        return score;
    }

    public Boolean isDead(ArrayList<Integer> dice, int fc){
        if(fc == 2){
            return false;
        }else{

            int skulls = 0;
            for(Integer die: dice){
                if(die == 3){
                    skulls++;
                }
            }
            if(fc == 71){
                skulls++;
            }else if(fc == 72){
                skulls +=2;
            }
            if(skulls ==3){
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
        int skulls = 0;
        for(int die: dice){
            if(die == 3){
                skulls++;
            }
        }
        if(this.fc == 71){
            skulls++;
        }
        if(this.fc == 72){
            skulls+=2;
        }
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
            if(dice.get(i) == 3 && this.fc == 2){
                this.fc = 8;
            }
        }
        System.out.print("Player "+ getTurn() + " re-rolling dice: " + reroll);
        for(int i: index){
            dice.set(i, -1);
        }
        ArrayList<String> resultString = new ArrayList<>();
        //player is in island of dead
        for(int i: outcome){
            resultString.add(this.dice.get(i));
        }
        System.out.println(", got "+resultString);
        for(int i: index){
            dice.remove((Integer) (-1));

        }
        dice.addAll(outcome);

        if(skulls >=4){
            this.canReroll = false;
            for(int die: outcome){
                if (die == 3){
                    canReroll = true;
                    break;
                }
            }
        }
        if(!canReroll){
            System.out.println("Player didn't roll any skull in island of the dead, cannot re-roll again.");
        }
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
        int skulls = 0;
        for(int die: dice){
            if(die == 3){
                skulls++;
            }
        }
        if(this.fc == 71){
            skulls++;
        }
        if(this.fc == 72){
            skulls+=2;
        }
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
            if(dice.get(i) == 3 && this.fc == 2){
                this.fc = 8;
            }
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
        if(skulls >=4){
            this.canReroll = false;
            for(int die: result){
                if (die == 3){
                    canReroll = true;
                    break;
                }
            }
        }
        if(!canReroll){
            System.out.println("Player didn't roll any skull in island of the dead, cannot re-roll again.");
        }
        return dice;
    }


    public int drawCard(){
        Random random = new Random();
        while(true){
            int card = random.nextInt(11);
            if(this.deck[card] > 0){
                this.deck[card]--;
                switch(card){
                    case 0:
                        card = 0;
                        break;
                    case 1:
                        card = 1;
                        break;
                    case 2:
                        card = 2;
                        break;
                    case 3:
                        card = 32;
                        break;
                    case 4:
                        card = 33;
                        break;
                    case 5:
                        card = 34;
                        break;
                    case 6:
                        card = 4;
                        break;
                    case 7:
                        card = 5;
                        break;
                    case 8:
                        card = 6;
                        break;
                    case 9:
                        card = 71;
                        break;
                    case 10:
                        card = 72;
                        break;

                }
                System.out.println("Player " + getTurn() + " draw fortune card :" + fortuneCard.get(card));
                return card;
            }
        }

    }

    public void broadcast(byte[] msg){
        try{
            for(int p: playerPort){
                DatagramPacket sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), p);
                send.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive(int length){
        byte[] msg = new byte[length];
        try{
            DatagramPacket receivePacket = new DatagramPacket(msg, length);
            receive.receive(receivePacket);
            return receivePacket.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        this.receive.close();
    }

    public void setFc(int fc){
        this.fc = fc;
    }

    public ArrayList<ArrayList<Integer>> nextCommand(ArrayList<ArrayList<Integer>> command){
        command.remove(0);
        return command;
    }

    public int sendScore(){
        int turnScore = countScore(rolled, fc);
        score[getTurn()-1]+=turnScore;
        boolean ending = false;
        for(int playerScore: score){
            if (playerScore >= 3000) {
                ending = true;
                break;
            }
        }
        if(!ending && countDown<3){
            countDown = 3;
        }
        if(ending){
            countDown--;
        }
        byte[] scoreByte = new byte[3];
        for(int i = 0; i< 3; i++){
            scoreByte[i] = (byte)(this.score[i]/100);
        }
        broadcast(scoreByte);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(countDown == 0){
            System.out.println("game ended");
        }
        return countDown;
    }

}
