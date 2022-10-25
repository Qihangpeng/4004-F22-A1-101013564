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
    private final int id;
    private final int port;
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
        ArrayList<String> reroll = new ArrayList<>();
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
        }
        System.out.print("Player "+ id + " re-rolling dice: " + reroll);
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

    //re-roll with cheat, outcome can be specified
    public ArrayList<Integer> re_roll(ArrayList<Integer> dice, ArrayList<Integer> index, ArrayList<Integer> outcome){
        ArrayList<String> reroll = new ArrayList<>();
        for(int i: index){
            reroll.add(this.dice.get(dice.get(i)));
        }
        System.out.print("Player "+ id + " re-rolling dice: " + reroll);
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
                System.out.println("Player " + this.id + " is dead(" + skulls+" skulls)");
                return true;
            }else{
                return false;
            }
        }
    }

    public int countScore(ArrayList<Integer> dice, int fc){
        int score = 0;

        if(isDead(dice, fc)){
            return 0;
        }else{
            int[] list = countDice(dice);
            switch(fc){
                case 0://treasure chest
                case 2://Sorceress
                case 72://2 skulls
                case 71://1 skull
                    score = calculateScore(list);
                    break;
                case 1://captain
                    score = 2 * calculateScore(list);
                    break;
                case 34://sea battle with 4 swords, 1000 points
                    score = calculateScore(list);
                    if(list[4] == 4){
                        score+=1000;
                    }else{
                        score-=1000;
                    }
                    break;
                case 32://sea battle with 2 swords, 300 points
                    score = calculateScore(list);
                    if(list[4] == 2){
                        score+=300;
                    }else{
                        score-=300;
                    }
                    break;
                case 33://sea battle with 3 swords, 500 points
                    score = calculateScore(list);
                    if(list[4] == 3){
                        score+=500;
                    }else{
                        score-=500;
                    }
                    break;
                case 4://gold
                    list[0]+=1;
                    score = calculateScore(list);
                    break;
                case 5://diamond
                    list[5]+=1;
                    score = calculateScore(list);
                    break;
                case 6://Monkey business
                    list[2]+=list[3];
                    list[3] = 0;
                    score = calculateScore(list);
                    break;
            }

            return score;
        }
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


    //base value of dice, without skull->die rule and fortune cards rule
    public int calculateScore(int[] dice){
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
                    score+=4000;
                    break;
            }
        }
        //count coins and diamonds
        score += (dice[0]+dice[5])*100;
        //full chest
        boolean fullChest = true;
        for(int die: dice){
            if(die <=3){
                fullChest = false;
                break;
            }
        }
        if(fullChest){
            score += 500;
        }
        return score;
    }


}
