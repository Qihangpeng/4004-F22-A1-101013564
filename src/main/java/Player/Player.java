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
    private int[] scoreCard;
    private boolean token;
    public Player(int id){
        token = false;
        fc = 0;
        fortuneCard = new HashMap<>();
        fortuneCard.put(0, "Treasure Chest");
        fortuneCard.put(1, "Captain");
        fortuneCard.put(2, "Sorceress");
        fortuneCard.put(32, "Sea Battle(2 swords, 300)");
        fortuneCard.put(33, "Sea Battle(3 swords, 500)");
        fortuneCard.put(34, "Sea Battle(4 swords, 1000)");
        fortuneCard.put(4, "Gold");
        fortuneCard.put(5, "Diamond");
        fortuneCard.put(6, "Monkey Business");
        fortuneCard.put(71, "One Skull");
        fortuneCard.put(72, "Two Skulls");
        scoreCard = new int[3];
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

    public void play(boolean cheat, ArrayList<ArrayList<Integer>> command){
        while(true){
            byte[] msg = receiveMessage(12);
            boolean yourTurn = (msg[0] == this.id);
            if(yourTurn){
                //player's turn
                System.out.println("it is your turn to play");
                fc = msg[1];
                if(fc == 2){
                    token = true;
                }

                System.out.println("Fortune card: " + fortuneCard.get((int)msg[1]));
                ArrayList<String> dice = new ArrayList<>();
                ArrayList<Integer> diceArray = new ArrayList<>();
                for(int i = 3; i<11; i++){
                    dice.add(this.dice.get((int)msg[i]));
                    diceArray.add((int)msg[i]);
                }
                System.out.println("You rolled: "+ dice);

                if(msg[2] == 1) {
                    System.out.println("You are dead");
                }else {
                    //check for island of dead
                    int skulls = 0;
                    for(int die: diceArray){
                        if(die == 3){
                            skulls++;
                        }
                    }
                    if(fc == 71){
                        skulls++;
                    }
                    if(fc == 72){
                        skulls+=2;
                    }
                    if(skulls >=4){
                        System.out.println("Player entered island of dead");
                    }
                    //player is not dead, ask if player want to re-roll
                    System.out.println("Do you wish to Re-roll our dice?");
                    System.out.println("1) Yes    2) No");
                    //use cheat command or player input
                    byte[] choice = new byte[1];
                    Scanner scanner = new Scanner(System.in);
                    if(cheat){
                        choice[0] = (byte)(int)command.get(0).get(0);
                        command = nextCommand(command);

                    }else{
                        choice[0] = scanner.nextByte();
                        scanner.nextLine();//consume \n
                    }
                    sendToServer(choice);
                    receiveMessage(1);
                    while(choice[0] == 1){
                        //player choose to re-roll
                        System.out.println("Please input the index of the dice you want to re-roll(separated by space): ");
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
                        String input = scanner.nextLine();
                        String[] index = input.split(" ");
                        byte[] reroll = new byte[8];
                        if(input.equals("")){
                            for(int i = 0; i< 8; i++){
                                reroll[i] = -1;
                            }
                        }else{
                            for(int i = 0; i< index.length; i++){
                                if(!index[i].equals("")){
                                    reroll[i] = (byte) Integer.parseInt(index[i]);
                                }
                            }
                            for(int i = index.length; i < 8; i++){
                                reroll[i] = -1;
                            }
                        }
                        if(!validateReroll(diceArray, reroll, fc)){
                            continue;
                        }
                        System.out.println();
                        sendToServer(reroll);
                        //receive new dice result
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
                        }else{
                            skulls = 0;
                            for(int die: rerolled){
                                if(die == 3){
                                    skulls++;
                                }
                            }
                            if(fc == 71){
                                skulls++;
                            }
                            if(fc == 72){
                                skulls+=2;
                            }
                            if(skulls >=4){
                                System.out.println("You entered island of dead");
                            }
                        }
                        //determine if player can re-roll again
                        byte[] can = receiveMessage(1);
                        if(can[0] == 0){
                            System.out.println("You didn't get any skull in island of the dead, cannot re-roll again.");
                            break;
                        }
                        System.out.println("Do you wish to re-roll again?");
                        System.out.println("1) Yes    2) No");
                        choice[0] = scanner.nextByte();
                        scanner.nextLine();
                        sendToServer(choice);
                        receiveMessage(1);
                    }
                }
            }else{
                //not player's turn
                System.out.println("it is player "+ msg[0]+" turn to play");
                System.out.println("Fortune card: " + fortuneCard.get((int)msg[1]));
                ArrayList<String> dice = new ArrayList<>();
                ArrayList<Integer> diceArray = new ArrayList<>();
                for(int i = 3; i<11; i++){
                    dice.add(this.dice.get((int)msg[i]));
                    diceArray.add((int)msg[i]);
                }
                System.out.println("Player " +msg[0] +" rolled: " + dice);
                if(msg[2] == 1){
                    //player is dead
                    System.out.println("Player "+msg[0]+" is dead");
                }else{
                    //player is not dead
                    //check for island of dead
                    int skulls = 0;
                    for (int die : diceArray) {
                        if (die == 3) {
                            skulls++;
                        }
                    }
                    if (fc == 71) {
                        skulls++;
                    }
                    if (fc == 72) {
                        skulls += 2;
                    }
                    if (skulls >= 4) {
                        System.out.println("Player " + msg[0]+" is in island of dead");
                    }
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
                        }else {
                            skulls = 0;
                            for (int die : rerolled) {
                                if (die == 3) {
                                    skulls++;
                                }
                            }
                            if (fc == 71) {
                                skulls++;
                            }
                            if (fc == 72) {
                                skulls += 2;
                            }
                            if (skulls >= 4) {
                                System.out.println("Player" + msg[0]+" are in island of dead");
                            }
                        }
                        byte[] can = receiveMessage(1);
                        if(can[0] == 0){
                            System.out.println("Player "+msg[0] +" didn't get any skull in island of the dead, cannot re-roll again.");
                            break;
                        }
                        choice = receiveMessage(1);
                    }
                }
            }
            //calculate score
            byte[] score = receiveMessage(3);
            int[] newScore = new int[3];
            for(int i =0; i< 3; i++){
                newScore[i] = score[i] *100;
            }
            if(msg[0] == this.id){
                if(score[msg[0]-1] == newScore[msg[0]-1]){
                    System.out.println("Your score is unchanged");
                }else{
                    if(score[msg[0]-1] > newScore[msg[0]-1]){
                        System.out.println("You reduced points due to lost of sea battle");
                    }
                }
            }else if(score[(msg[0]%3)] > newScore[msg[0]%3]){
                System.out.println("You reduced points due to island of dead");
            }
            System.out.println("Player " + msg[0] + " scored " + (newScore[msg[0]-1] - scoreCard[msg[0] -1]));
            System.out.println("________________________________________");
            System.out.println("| Player |    1    |    2    |    3    |");
            System.out.println("----------------------------------------");
            System.out.printf( "| Score  |   %4d  |  %4d   |  %4d   |\n", newScore[0], newScore[1], newScore[2]);
            System.out.println("----------------------------------------");
            scoreCard = newScore;
            byte[] remaining = receiveMessage(1);
            if(remaining[0] < 3){
                if(remaining[0] == 0){
                    byte[] winner = receiveMessage(1);
                    System.out.println("Player " + winner[0] + " won the game");
                    break;
                }else{
                    System.out.println("game end's in " + remaining[0]+ " turn(s)");
                }
            }

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

    //make sure player cannot re-roll skull if fc is not sorceress, and can only re-roll 1 skull if fc is sorceress
    public boolean validateReroll(ArrayList<Integer> dice, byte[] index, int fc){
        boolean valid = true;
        int skulls = 0;
        int count = 0;
        for(int i: index){
            if(i>=0){
                if(dice.get(i) == 3){
                    skulls++;
                }
                count++;
            }

        }
        if(fc != 2){
            if(skulls != 0){
                System.out.println("You cannot re-roll skulls unless you have fc: sorceress");
                valid = false;
            }
            if(count==1){
                System.out.println("You have to re-roll at least 2 dice at once");
                valid = false;
            }
        }else{
            if(skulls >=2){
                System.out.println("You can only re-roll one skull");
                valid = false;
            }
            if(!token){
                System.out.println("You have already re-rolled a skull");
                valid = false;
            }
            if(count==1 && skulls !=1){
                System.out.println("You have to re-roll at least two dice at once, unless you are re-rolling skull");
                valid = false;
            }
        }
        //re-rolling a skull, consume token
        if(valid){
            if(skulls ==1){
                this.token = false;
            }
        }
        return valid;
    }

    public void close(){
        receive.close();
    }
    public ArrayList<ArrayList<Integer>> nextCommand(ArrayList<ArrayList<Integer>> command){
        command.remove(0);
        return command;
    }
}
