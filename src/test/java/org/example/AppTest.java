package org.example;

import static org.junit.Assert.assertTrue;
import Player.Player;
import Server.Server;
import org.junit.Test;

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
                Player a = new Player((byte) 1);
                a.connect(4000);
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player((byte) 2);
                b.connect(4000);
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player((byte) 3);
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
        Player a = new Player((byte)1);
        System.out.println(a.score());
    }

    @Test
    public void drawCard(){
        Player a = new Player((byte)1);
        for(int i = 0; i<10; i++){
            System.out.println(a.drawCard());
        }
    }

    @Test
    public void rollDice(){
        Player a = new Player((byte) 1);
        System.out.println(a.rollDice());
    }

    @Test
    public void re_roll(){
        Player a = new Player((byte)1);
        ArrayList<String> dice = new ArrayList<>();
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


}
