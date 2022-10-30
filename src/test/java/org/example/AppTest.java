package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import Player.Player;
import Server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
                Server s = new Server(4000, false);
                s.connect();
                s.close();
            }
        });
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                Player a = new Player(1);
                a.connect();
                a.close();
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player(2);
                b.connect();
                b.close();
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player(3);
                c.connect();
                c.close();
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
    public void rollDice(){
        Server s = new Server(4000, false);
        System.out.println(s.rollDice());
        s.close();
    }

    @Test
    public void re_roll(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice());
        ArrayList<Integer> index = generateDice(0,1,2,3,4,5,6,7);
        dice = s.re_roll(dice, index);
        System.out.println(dice);
        s.close();
    }

    @Test
    public void countDice(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice());
        System.out.println(Arrays.toString(s.countDice(dice)));
        s.close();
    }

    @Test
    public void countScore(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice(),s.rollDice());
        System.out.println(s.countScore(dice, 0));
        s.close();
    }

    @Test
    public void score(){
        Player a = new Player(1);
        System.out.println(a.score());
        a.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        Server s = new Server(4000, false);
        assertEquals(0,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    //roll 1 skull, 4 parrots, 3 swords, re-roll 3 swords, get 2 skulls 1 sword  die
    public void row46(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,4,4,4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(3);
        outcome.add(4);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertTrue(s.isDead(result, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 skulls, 4 parrots, 2 swords, re-roll swords, get 1 skull 1 sword  die
    public void row47(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,3,4,4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(4);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertTrue(s.isDead(result, 0));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    //roll 1 skull, 4 parrots, 3 swords, re-roll swords, get 1 skull 2 monkeys,
    //      re-roll 2 monkeys, get 1 skull 1 monkey and die
    public void row48(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,1,1,3,4,4, 4);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(2);
        outcome.add(2);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertFalse(s.isDead(result, 4));
        index.remove(0);
        outcome.remove(1);
        result = s.re_roll(result, index, outcome);
        Assertions.assertTrue(s.isDead(result, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 2 parrots, 3 swords, 2 coins, re-roll parrots get 2 coins
    //      re-roll 3 swords, get 3 coins (SC 4000 for seq of 8 (with FC coin) + 8x100=800 = 4800)
    public void row50(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3, 1,1,4,4,4,0,0);
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(0);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertFalse(s.isDead(result, 4));
        index.clear();
        index.add(1);
        index.add(2);
        index.add(3);
        outcome.clear();
        outcome.add(0);
        outcome.add(0);
        outcome.add(0);
        result = s.re_roll(result, index, outcome);
        System.out.println(result);
        Assertions.assertFalse(s.isDead(result, 4));
        assertEquals( 4800,s.countScore(result, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //score first roll with 2 (monkeys/parrot/diamonds/coins) and FC is captain (SC 800)
    public void row52(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0, 0,1,1,2,2,5,5);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 800,s.countScore(dice, 1));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 (monkeys/skulls/swords/parrots), re-roll parrots and get 1 sword & 1 monkey (SC 300 since FC is coin)
    public void row53(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,2,2,3,3,4,4);
        Assertions.assertFalse( s.isDead(dice, 4));

        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(2);
        outcome.add(4);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(result, 4));
        assertEquals( 300,s.countScore(result, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 3 (monkey, swords) + 2 skulls and score   (SC 300)
    public void row54(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,3,3);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 300,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 3 diamonds, 2 skulls, 1 monkey, 1 sword, 1 parrot, score (diamonds = 100 + 300 points)   (SC 500)
    public void row55(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(5,5,5,3,3,2,4,1);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 500,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 4 coins, 2 skulls, 2 swords and score (coins: 200 + 400 points) with FC is a diamond (SC 700)
    public void row56(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,3,3,4,4);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 700,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 3 swords, 4 parrots, 1 skull and score (SC 100+200+100= 400)
    public void row57(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(4,4,4,3,1,1,1,1);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 400,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 2 coins/parrots & 3 swords, re-roll parrots, get 1 coin and 1 sword, score (SC = 200+400+200 = 800)
    public void row58(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,1,1,0,0,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 4));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(4);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(result, 4));
        assertEquals( 800,s.countScore(result, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //same as previous row but with captain fortune card  (SC = (100 + 300 + 200)*2 = 1200)
    public void row59(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,1,1,0,0,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 1));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(4);
        ArrayList<Integer> result = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(result, 1));
        assertEquals( 1200,s.countScore(result, 1));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 2 (monkeys/parrots) 3 swords, re-roll 2 monkeys, get 1 skull 1 sword,
    //         then re-roll parrots get 1 sword 1 monkey (SC 600)
    public void row60(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,1,1,2,2,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 5));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(1);
        index.add(2);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(4);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 4));
        outcome.set(0,2);
        outcome.set(1,4);
        dice = s.re_roll(dice, index, outcome);
        assertEquals( 600,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //score set of 6 monkeys and 2 skulls on first roll (SC 1100)
    public void row62(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,2,2,2,3,3);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 1100,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    //score set of 7 parrots and 1 skull on first roll (SC 2100)
    public void row63(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,1,1,1,1,1,3);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 2100,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //score set of 8 coins on first roll (SC 5400)  seq of 8 + 9 coins(FC is coin) +  full chest  (no extra points for 9 coins)
    public void row64(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,0,0,0,0);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 5400,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //score set of 8 coins on first roll and FC is diamond (SC 5400)
    public void row65(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,0,0,0,0);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 5400,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //score set of 8 swords on first roll and FC is captain (SC 4500x2 = 9000) since full chest
    public void row66(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(4,4,4,4,4,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 9000,s.countScore(dice, 1));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 6 monkeys and 2 swords, re-roll swords, get 2 monkeys, score (SC 4600 because of FC is coin and full chest)
    public void row67(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,2,2,2,4,4);
        Assertions.assertFalse( s.isDead(dice, 4));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(2);
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 4600,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 (monkeys/skulls/swords/parrots), re-roll parrots, get 2 diamonds, score with FC is diamond (SC 400)
    public void row68(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,2,2,3,3,4,4);
        Assertions.assertFalse( s.isDead(dice, 5));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(5);
        outcome.add(5);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 400,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 (monkeys, skulls, swords), 1 diamond, 1 parrot, re-roll 2 monkeys, get 2 diamonds, score 500
    public void row69(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,3,3,4,4,5,1);
        Assertions.assertFalse( s.isDead(dice, 5));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(5);
        outcome.add(5);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 500,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 2 coins, 1 (monkey/parrot), 3 swords, re-roll 3 swords, get 1 (coin/monkey/parrot)  (SC 600)
    public void row70(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3, 0, 0, 1, 2, 4, 4, 4);
        Assertions.assertFalse( s.isDead(dice, 4));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(1);
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 600,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 2 coins, 1 (monkey/parrot), 3 swords,
    // re-roll swords, get 1 (coin/monkey/parrot) with FC is diamond (SC 500)
    public void row71(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,0,0,1,2,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 5));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(0);
        outcome.add(1);
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 500,s.countScore(dice, 5));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //get 4 monkeys, 2 coins and 2 skulls with FC coin. Score 600
    public void row72(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,3,3,2,2,2,2);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 600,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 diamonds, 1 (sword/monkey/coin), 3 parrots, re-roll 3 parrots, get 1 skull, 2 monkeys, re-roll skull, get monkey (SC 500)
    public void row77(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(5,5,4,2,0,1,1,1);
        Assertions.assertFalse( s.isDead(dice, 1));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(2);
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 4));
        index.clear();
        index.add(5);
        outcome.clear();
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 500,s.countScore(dice, 2));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 3 skulls, 3 parrots, 2 swords, re-roll skull, get parrot, re-roll 2 swords, get parrots, score (SC 1000)
    public void row78(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3, 3, 3, 1, 1, 1, 4, 4);
        Assertions.assertFalse( s.isDead(dice, 2));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(1);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 2));
        index.clear();
        index.add(6);
        index.add(5);
        outcome.add(1);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 2));
        assertEquals( 1000,s.countScore(dice, 2));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 1 skull, 4 parrots, 3 monkeys, re-roll 3 monkeys, get 1 skull, 2 parrots, re-roll skull, get parrot, score (SC 2000)
    public void row79(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,1,1,1,1,2,2,2);
        Assertions.assertFalse( s.isDead(dice, 2));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        ArrayList<Integer> outcome = new ArrayList<>();
        outcome.add(3);
        outcome.add(1);
        outcome.add(1);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 2));
        index.clear();
        index.add(5);
        outcome.clear();
        outcome.add(1);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 2));
        assertEquals( 2000,s.countScore(dice, 2));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    //roll 3 monkeys 3 parrots  1 skull 1 coin  SC = 1100  (i.e., sequence of of 6 + coin)
    public void row82(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,1,1,1,3,0);
        Assertions.assertFalse( s.isDead(dice, 6));
        assertEquals( 1100,s.countScore(dice, 6));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 2 (monkeys/swords/parrots/coins), re-roll 2 swords, get 1 monkey, 1 parrot, score 1700
    public void row83(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,4,4,2,2,0,0);
        Assertions.assertFalse( s.isDead(dice, 6));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(2);
        index.add(3);
        outcome.add(1);
        outcome.add(2);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 6));
        assertEquals( 1700,s.countScore(dice, 6));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //roll 3 skulls, 3 monkeys, 2 parrots => die scoring 0
    public void row84(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,3,3,2,2,2,1,1);
        Assertions.assertTrue( s.isDead(dice, 6));
        assertEquals( 0,s.countScore(dice, 6));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    //3 monkeys, 3 swords, 1 diamond, 1 parrot FC: coin   => SC 400  (ie no bonus)
    public void row97(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,5,1);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 400,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //3 monkeys, 3 swords, 2 coins FC: captain   => SC (100+100+200+500)*2 =  1800
    public void row98(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,0,0);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 1800,s.countScore(dice, 1));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //3 monkeys, 4 swords, 1 diamond, FC: coin   => SC 1000  (ie 100++200+100+100+bonus)
    public void row99(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,4,5);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 1000,s.countScore(dice, 4));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC: 2 sword sea battle, first  roll:  4 monkeys, 1 sword, 2 parrots and a coin
    //     then re-roll 2 parrots and get 2nd coin and 2nd sword
    //     score is: 200 (coins) + 200 (monkeys) + 300 (swords of battle) + 500 (full chest) = 1200
    public void row102(){
        Server s = new Server(4000, false);
        s.setFc(32);
        ArrayList<Integer> dice = generateDice(2,2,2,2,4,1,1,0);
        Assertions.assertFalse( s.isDead(dice, 32));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(5);
        index.add(6);
        outcome.add(4);
        outcome.add(0);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 32));
        assertEquals( 1200,s.countScore(dice, 32));
        s.close();

    }

    @Test
    //FC: monkey business and roll 2 monkeys, 1 parrot, 2 coins, 3 diamonds   SC 1200
    public void row103(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,1,0,0,5,5,5);
        Assertions.assertFalse( s.isDead(dice, 6));
        assertEquals( 1200,s.countScore(dice, 6));
        s.close();

    }

    @Test
    //roll one skull and 7 swords with FC with two skulls => die
    public void row106(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,4,4,4,4,4,4,4);
        Assertions.assertTrue( s.isDead(dice, 72));
        assertEquals( 0,s.countScore(dice, 72));
        s.close();

    }

    @Test
    //roll 2 skulls and 6 swords with FC with 1 skull  => die
    public void row107(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,3,4,4,4,4,4,4);
        Assertions.assertTrue( s.isDead(dice, 71));
        assertEquals( 0,s.countScore(dice, 71));
        s.close();

    }

    @Test
    //roll 2 skulls  3(parrots/monkeys) with FC with two skulls: re-roll 3 parrots get 2 skulls, 1 sword
    //  re-roll sword and 3 monkeys, get 3 skulls and 1 sword, stop => -900 for other players (no negative score) & you score 0
    public void row109(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,3,1,1,1,2,2,2);
        Assertions.assertFalse( s.isDead(dice, 72));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(2);
        index.add(3);
        index.add(4);
        outcome.add(3);
        outcome.add(3);
        outcome.add(4);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 72));
        index.add(7);
        outcome.add(3);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 72));
        assertEquals( 0,s.countScore(dice, 72));
        s.close();

    }


    @Test
    //roll 5 skulls, 3 monkeys with FC Captain, re-roll 3 monkeys, get 2 skulls, 1 coin, stop => -1400 for other players
    public void row110(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,3,3,3,3,2,2,2);
        Assertions.assertFalse( s.isDead(dice, 1));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(5);
        index.add(6);
        index.add(7);
        outcome.add(3);
        outcome.add(3);
        outcome.add(0);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 0,s.countScore(dice, 1));
        s.close();


    }


    @Test
    //roll 3 skulls and 5 swords with FC with two skulls: re-roll 5 swords, get 5 coins, must stop  => -500 for other players
    public void row111(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(3,3,3,4,4,4,4,4);
        s.setFc(72);
        Assertions.assertFalse( s.isDead(dice, 72));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(3);
        index.add(4);
        index.add(5);
        index.add(6);
        index.add(7);
        outcome.add(0);
        outcome.add(0);
        outcome.add(0);
        outcome.add(0);
        outcome.add(0);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 72));
        assertEquals( 0,s.countScore(dice, 72));
        s.close();


    }

    @Test
    //FC 2 swords, roll 4 monkeys, 3 skulls & 1 sword and die   => die and lose 300 points
    public void row114(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,2,3,3,3,4);
        Assertions.assertTrue( s.isDead(dice, 32));
        assertEquals( -300,s.countScore(dice, 32));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 3 swords, have 2 swords, 2 skulls and 4 parrots, re-roll 4 parrots, get 4 skulls=> die and lose 500 points
    public void row115(){
        Server s = new Server(4000, false);
        s.setFc(33);
        ArrayList<Integer> dice = generateDice(4,4,3,3,1,1,1,1);
        Assertions.assertFalse( s.isDead(dice, 33));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(4);
        index.add(5);
        index.add(6);
        index.add(7);
        outcome.add(3);
        outcome.add(3);
        outcome.add(3);
        outcome.add(3);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertTrue( s.isDead(dice, 33));
        assertEquals( -500,s.countScore(dice, 33));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 4 swords, die on first roll with 2 monkeys, 3 (skulls/swords)  => die and lose 1000 points
    public void row116(){
        Server s = new Server(4000, false);
        s.setFc(34);
        ArrayList<Integer> dice = generateDice(2,2,3,3,3,4,4,4);
        Assertions.assertTrue( s.isDead(dice, 34));
        assertEquals( -1000,s.countScore(dice, 34));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 2 swords, roll 3 monkeys 2 swords, 1 coin, 2 parrots  SC = 100 + 100 + 300 = 500
    public void row117(){
        Server s = new Server(4000, false);
        s.setFc(32);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,0,1,1);
        Assertions.assertFalse( s.isDead(dice, 32));
        assertEquals( 500,s.countScore(dice, 32));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 2 swords, roll 4 monkeys 1 sword, 1 skull & 2 parrots
    //  then re-roll 2 parrots and get 1 sword and 1 skull   SC = 200 +  300 = 500
    public void row119(){
        Server s = new Server(4000, false);
        s.setFc(32);
        ArrayList<Integer> dice = generateDice(2,2,2,2,4,3,1,1);
        Assertions.assertFalse( s.isDead(dice, 32));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(6);
        index.add(7);
        outcome.add(3);
        outcome.add(4);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 32));
        assertEquals( 500,s.countScore(dice, 32));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 3 swords, roll 3 monkeys 4 swords 1 skull SC = 100 + 200 + 500 = 800
    public void row120(){
        Server s = new Server(4000, false);
        s.setFc(33);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,4,3);
        Assertions.assertFalse( s.isDead(dice, 33));
        assertEquals( 800,s.countScore(dice, 33));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 3 swords, roll 4 monkeys 2 swords 2 skulls
    //         then re-roll 4 monkeys and get  2 skulls and 2 swords   => die and lose 500 points
    public void row122(){
        Server s = new Server(4000, false);
        s.setFc(33);
        ArrayList<Integer> dice = generateDice(2,2,2,2,3,3,4,4);
        Assertions.assertFalse( s.isDead(dice, 33));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(0);
        index.add(1);
        index.add(2);
        index.add(3);
        outcome.add(3);
        outcome.add(3);
        outcome.add(4);
        outcome.add(4);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertTrue( s.isDead(dice, 33));
        assertEquals( -500,s.countScore(dice, 33));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    //FC 4 swords, roll 3 monkeys 4 swords 1 skull  SC = 100 +200 + 1000 = 1300
    public void row123(){
        Server s = new Server(4000, false);
        s.setFc(34);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,4,3);
        Assertions.assertFalse( s.isDead(dice, 34));
        assertEquals( 1300,s.countScore(dice, 34));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FC 4 swords, roll 3 monkeys, 1 sword, 1 skull, 1 diamond, 2 parrots
    //  then re-roll 2 parrots and get 2 swords thus you have 3 monkeys, 3 swords, 1 diamond, 1 skull
    //  then re-roll 3 monkeys and get  1 sword and 2 parrots  SC = 200 + 100 + 1000 = 1300
    public void row126(){
        Server s = new Server(4000, false);
        s.setFc(34);
        ArrayList<Integer> dice = generateDice(2,2,2,4,3,5,1,1);
        Assertions.assertFalse( s.isDead(dice, 34));
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> outcome = new ArrayList<>();
        index.add(6);
        index.add(7);
        outcome.add(4);
        outcome.add(4);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 34));

        index.clear();
        outcome.clear();
        index.add(0);
        index.add(1);
        index.add(2);
        outcome.add(4);
        outcome.add(1);
        outcome.add(1);
        dice = s.re_roll(dice, index, outcome);
        Assertions.assertFalse( s.isDead(dice, 34));
        assertEquals( 1300,s.countScore(dice, 34));
        s.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //player1 rolls 7 swords + 1 skull with FC captain (gets 4000 points - could win)
    //then player2 rolls 7 swords 1 skull  with FC 1 skull (2000)
    //then player3 scores 0 (3 skulls, 5 monkeys, FC coin) => game stops and declares player 1 wins
    public void row132(){
        Thread st = new Thread(new Runnable() {
            @Override
            public void run() {
                Server s = new Server(4000, false);
                s.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> fc1 = new ArrayList<>();
                fc1.add(1);
                ArrayList<Integer> dice1 = generateDice(4,4,4,4,4,4,4,3);
                ArrayList<Integer> fc2 = new ArrayList<>();
                fc2.add(71);
                ArrayList<Integer> dice2 = generateDice(4,4,4,4,4,4,4,3);
                ArrayList<Integer> fc3 = new ArrayList<>();
                fc3.add(4);
                ArrayList<Integer> dice3 = generateDice(3,3,3,2,2,2,2,2);
                command.add(fc1);
                command.add(dice1);
                command.add(fc2);
                command.add(dice2);
                command.add(fc3);
                command.add(dice3);
                s.gameStart(true, command);
                s.close();
            }
        });
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                Player a = new Player(1);
                a.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                a.play(true, command);
                a.close();
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player(2);
                b.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                b.play(true, command);
                b.close();
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player(3);
                c.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                c.play(true, command);
                c.close();
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //player1 rolls 7 swords + 1 skull with FC captain (gets 4000 points - could win)
    //then player2 scores 0 (3 skulls, 5 monkeys, FC coin)
    //then player3 rolls 6 skulls & 2 parrots with FC  Captain, then stops => deduction of (600x2)= 1200 points
    //      => score of player1 goes to 2800, scoreof player 2 stays at 0
    //then player 1 rolls 4 monkeys, 4 parrots with FC coin, scores 1000 points to get him to 3800 (again can win)
    //then player2 scores 0 (3 skulls, 5 monkeys, FC Captain) and player3 scores 0 (2 skulls, 6 monkeys, FC 1 skull)
    //player 1 wins
    public void row140(){
        Thread st = new Thread(new Runnable() {
            @Override
            public void run() {
                Server s = new Server(4000, false);
                s.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> fc1 = new ArrayList<>();
                fc1.add(1);
                ArrayList<Integer> dice1 = generateDice(4,4,4,4,4,4,4,3);
                ArrayList<Integer> fc2 = new ArrayList<>();
                fc2.add(4);
                ArrayList<Integer> dice2 = generateDice(3,3,3,2,2,2,2,2);
                ArrayList<Integer> fc3 = new ArrayList<>();
                fc3.add(1);
                ArrayList<Integer> dice3 = generateDice(3,3,3,3,3,3,1,1);
                ArrayList<Integer> fc4 = new ArrayList<>();
                fc4.add(4);
                ArrayList<Integer> dice4 = generateDice(2,2,2,2,1,1,1,1);
                ArrayList<Integer> fc5 = new ArrayList<>();
                fc5.add(1);
                ArrayList<Integer> dice5 = generateDice(3,3,3,2,2,2,2,2);
                ArrayList<Integer> fc6 = new ArrayList<>();
                fc6.add(71);
                ArrayList<Integer> dice6 = generateDice(3,3,2,2,2,2,2,2);
                command.add(fc1);
                command.add(dice1);
                command.add(fc2);
                command.add(dice2);
                command.add(fc3);
                command.add(dice3);
                command.add(fc4);
                command.add(dice4);
                command.add(fc5);
                command.add(dice5);
                command.add(fc6);
                command.add(dice6);
                s.gameStart(true, command);
                s.close();
            }
        });
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                Player a = new Player(1);
                a.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                command.add(choice);
                a.play(true, command);
                a.close();
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player(2);
                b.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                command.add(choice);
                b.play(true, command);
                b.close();
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player(3);
                c.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                c.play(true, command);
                c.close();
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
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //player 1 scores 0 with (3 skulls, 5 monkeys, FC Captain)
    //player2 rolls 7 swords + 1 skull with FC captain (gets 4000 points - could win)
    // then player3 scores 0 with FC 2 skulls and a roll of 1 skull & 7 swords
    //    then player 1 has FC Captain rolls 8 swords and thus gets 9000 points     => player 1 WINS
    public void row145(){
        Thread st = new Thread(new Runnable() {
            @Override
            public void run() {
                Server s = new Server(4000, false);
                s.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> fc1 = new ArrayList<>();
                fc1.add(1);
                ArrayList<Integer> dice1 = generateDice(3,3,3,5,5,5,5,5);
                ArrayList<Integer> fc2 = new ArrayList<>();
                fc2.add(1);
                ArrayList<Integer> dice2 = generateDice(4,4,4,4,4,4,4,3);
                ArrayList<Integer> fc3 = new ArrayList<>();
                fc3.add(72);
                ArrayList<Integer> dice3 = generateDice(3,4,4,4,4,4,4,4);
                ArrayList<Integer> fc4 = new ArrayList<>();
                fc4.add(1);
                ArrayList<Integer> dice4 = generateDice(4,4,4,4,4,4,4,4);
                command.add(fc1);
                command.add(dice1);
                command.add(fc2);
                command.add(dice2);
                command.add(fc3);
                command.add(dice3);
                command.add(fc4);
                command.add(dice4);
                s.gameStart(true, command);
                s.close();
            }
        });
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                Player a = new Player(1);
                a.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                command.add(choice);

                a.play(true, command);
                a.close();
            }
        });Thread bt = new Thread(new Runnable() {
            @Override
            public void run() {
                Player b = new Player(2);
                b.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                command.add(choice);
                b.play(true, command);
                b.close();
            }
        });Thread ct = new Thread(new Runnable() {
            @Override
            public void run() {
                Player c = new Player(3);
                c.connect();
                ArrayList<ArrayList<Integer>> command = new ArrayList<>();
                ArrayList<Integer> choice = new ArrayList<>();
                choice.add(2);
                command.add(choice);
                command.add(choice);
                c.play(true, command);
                c.close();
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
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
