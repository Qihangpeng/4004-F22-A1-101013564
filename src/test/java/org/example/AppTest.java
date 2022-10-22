package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import Player.Player;
import Server.Server;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    public ArrayList<Integer> generateDice(int a, int b, int c, int d, int e, int f, int g, int h){
        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(a);
        dice.add(b);
        dice.add(c);
        dice.add(d);
        dice.add(e);
        dice.add(f);
        dice.add(g);
        dice.add(h);
        return dice;
    }
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    //test server and players initialization and connections between server and player

    @Test
    public void startGame(){
        Thread st = new Thread(new Runnable() {
            @Override
            public void run() {
                Server s = new Server(4000);
                s.connect();
            }
        });
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                Player a = new Player(1);
                a.connect(4000);
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player(2);
                b.connect(4000);
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player(3);
                c.connect(4000);
            }
        });
        st.start();
        at.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bt.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ct.start();
    }

    @Test
    public void score(){
        Player a = new Player(1);
        System.out.println(a.score());
    }

    @Test
    public void drawCard(){
        Player a = new Player(1);
        for(int i = 0; i<10; i++){
            System.out.println(a.drawCard());
        }
    }

    @Test
    public void rollDice(){
        Player a = new Player(1);
        System.out.println(a.rollDice());
    }

    @Test
    public void re_roll(){
        Player a = new Player(1);
        ArrayList<Integer> dice = new ArrayList<>();
        Random random = new Random();
        ArrayList<Integer> index = new ArrayList<>();
        //generate 8 random roll result
        for(int i = 0; i < 8; i++){
            dice.add(a.rollDice());
        }
        //generate random number of random indices to re-roll
        for(int i = 0; i<random.nextInt(8); i++){
            index.add(random.nextInt(8));
        }
        System.out.println("result:          " + dice);
        System.out.println("re-roll indices: " + index);
        dice = a.re_roll(dice, index);
        System.out.println("new result:      " + dice);
    }

    @Test
    public void countTurn(){
        Server s = new Server(4000);
        System.out.println(s.getTurn());
        for(int i = 0; i < 20; i++){
            System.out.println(s.nextTurn());
        }
    }

    //        dice.put(0, "Coin");
    //        dice.put(1, "Fish");
    //        dice.put(2, "Monkey");
    //        dice.put(3, "Skull");
    //        dice.put(4,"Swords");
    //        dice.put(5, "Diamond");
    //dice with corresponding number
    @Test
    //die with 3 skulls 5 swords on first roll: player gets a score of 0
    public void r45(){
        ArrayList<Integer> dice = generateDice(3,3,3,5,5,5,5,5);
        Server s = new Server(4000);
        assertEquals(s.countDice(dice),0);
    }






}
