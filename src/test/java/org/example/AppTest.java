package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import Player.Player;
import Server.Server;
import org.junit.jupiter.api.Assertions;
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
    //        dice.put(1, "parrot");
    //        dice.put(2, "Monkey");
    //        dice.put(3, "Skull");
    //        dice.put(4,"Swords");
    //        dice.put(5, "Diamond");
    //dice with corresponding number
    @Test
    //die with 3 skulls 5 swords on first roll: player gets a score of 0
    public void row45(){
        ArrayList<Integer> dice = generateDice(3,3,3,5,5,5,5,5);
        Player a = new Player(1);
        assertEquals(0,a.countScore(dice, 9));
    }


    @Test
    //roll 1 skull, 4 parrots, 3 swords, re-roll 3 swords, get 2 skulls 1 sword  die
    public void row46(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,4,4,4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(3);
        outcome.add(4);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertTrue(a.isDead(result, 0));
    }

    @Test
    //roll 2 skulls, 4 parrots, 2 swords, re-roll swords, get 1 skull 1 sword  die
    public void row47(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,3,4,4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(4);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertTrue(a.isDead(result, 0));
    }

    @Test
    //roll 1 skull, 4 parrots, 3 swords, re-roll swords, get 1 skull 2 monkeys,
    //      re-roll 2 monkeys, get 1 skull 1 monkey and die
    public void row48(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,4,4, 4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(2);
        outcome.add(2);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertFalse(a.isDead(result, 0));
        index.remove(0);
        outcome.remove(1);
        result = a.re_roll(result, index, outcome);
        Assertions.assertTrue(a.isDead(result, 0));
    }

    @Test
    //roll 1 skull, 2 parrots, 3 swords, 2 coins, re-roll parrots get 2 coins
    //      re-roll 3 swords, get 3 coins (SC 4000 for seq of 8 (with FC coin) + 8x100=800 = 4800)
    public void row50(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(3, 1,1,4,4,4,0,0);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(0);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertFalse(a.isDead(result, 4));
        index.clear();
        index.add(1);
        index.add(2);
        index.add(3);
        outcome.clear();
        outcome.add(0);
        outcome.add(0);
        outcome.add(0);
        result = a.re_roll(result, index, outcome);
        System.out.println(result);
        Assertions.assertFalse(a.isDead(result, 4));
        assertEquals( 4800,a.countScore(result, 4));
    }

    @Test
    //score first roll with 2 (monkeys/parrot/diamonds/coins) and FC is captain (SC 800)
    public void row52(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(0, 0,1,1,2,2,5,5);
        Assertions.assertFalse( a.isDead(dice, 1));
        assertEquals( 800,a.countScore(dice, 1));

    }

    @Test
    //roll 2 (monkeys/skulls/swords/parrots), re-roll parrots and get 1 sword & 1 monkey (SC 300 since FC is coin)
    public void row53(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(1,1,2,2,3,3,4,4);
        Assertions.assertFalse( a.isDead(dice, 4));

        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(2);
        outcome.add(4);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertFalse( a.isDead(result, 4));
        assertEquals( 300,a.countScore(result, 4));

    }

    @Test
    //roll 3 (monkey, swords) + 2 skulls and score   (SC 300)
    public void row54(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,3,3);
        Assertions.assertFalse( a.isDead(dice, 4));
        assertEquals( 300,a.countScore(dice, 4));

    }

    @Test
    //roll 3 diamonds, 2 skulls, 1 monkey, 1 sword, 1 parrot, score (diamonds = 100 + 300 points)   (SC 500)
    public void row55(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(5,5,5,3,3,2,4,1);
        Assertions.assertFalse( a.isDead(dice, 4));
        assertEquals( 500,a.countScore(dice, 4));
    }

    @Test
    //roll 4 coins, 2 skulls, 2 swords and score (coins: 200 + 400 points) with FC is a diamond (SC 700)
    public void row56(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(0,0,0,0,3,3,4,4);
        Assertions.assertFalse( a.isDead(dice, 5));
        assertEquals( 700,a.countScore(dice, 5));
    }

    @Test
    //roll 3 swords, 4 parrots, 1 skull and score (SC 100+200+100= 400)
    public void row57(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(4,4,4,3,1,1,1,1);
        Assertions.assertFalse( a.isDead(dice, 5));
        assertEquals( 400,a.countScore(dice, 5));
    }

    @Test
    //roll 1 skull, 2 coins/parrots & 3 swords, re-roll parrots, get 1 coin and 1 sword, score (SC = 200+400+200 = 800)
    public void row58(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(3,1,1,0,0,4,4,4);
        Assertions.assertFalse( a.isDead(dice, 4));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(4);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertFalse( a.isDead(result, 4));
        assertEquals( 800,a.countScore(result, 4));
    }

    @Test
    //same as previous row but with captain fortune card  (SC = (100 + 300 + 200)*2 = 1200)
    public void row59(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(3,1,1,0,0,4,4,4);
        Assertions.assertFalse( a.isDead(dice, 1));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(4);
        ArrayList<Integer> result = a.re_roll(dice, index, outcome);
        Assertions.assertFalse( a.isDead(result, 1));
        assertEquals( 1200,a.countScore(result, 1));
    }

    @Test
    //roll 1 skull, 2 (monkeys/parrots) 3 swords, re-roll 2 monkeys, get 1 skull 1 sword,
    //         then re-roll parrots get 1 sword 1 monkey (SC 600)
    public void row60(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(3,1,1,2,2,4,4,4);
        Assertions.assertFalse( a.isDead(dice, 5));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(4);
        dice = a.re_roll(dice, index, outcome);
        Assertions.assertFalse( a.isDead(dice, 5));
        outcome.set(0,2);
        outcome.set(1,4);
        dice = a.re_roll(dice, index, outcome);
        assertEquals( 600,a.countScore(dice, 5));
    }

    @Test
    //score set of 6 monkeys and 2 skulls on first roll (SC 1100)
    public void row62(){
        Player a = new Player(1);
        ArrayList<Integer> dice = generateDice(2,2,2,2,2,2,3,3);
        Assertions.assertFalse( a.isDead(dice, 5));
        assertEquals( 1100,a.countScore(dice, 5));

    }








}
