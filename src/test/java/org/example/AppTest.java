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
    public void score(){
        Player a = new Player(1);
        System.out.println(a.score());
        a.close();
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

    }

    @Test
    //score first roll with 2 (monkeys/parrot/diamonds/coins) and FC is captain (SC 800)
    public void row52(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0, 0,1,1,2,2,5,5);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 800,s.countScore(dice, 1));
        s.close();

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

    }

    @Test
    //roll 3 (monkey, swords) + 2 skulls and score   (SC 300)
    public void row54(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,4,4,4,3,3);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 300,s.countScore(dice, 4));
        s.close();

    }

    @Test
    //roll 3 diamonds, 2 skulls, 1 monkey, 1 sword, 1 parrot, score (diamonds = 100 + 300 points)   (SC 500)
    public void row55(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(5,5,5,3,3,2,4,1);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 500,s.countScore(dice, 4));
        s.close();

    }

    @Test
    //roll 4 coins, 2 skulls, 2 swords and score (coins: 200 + 400 points) with FC is a diamond (SC 700)
    public void row56(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,3,3,4,4);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 700,s.countScore(dice, 5));
        s.close();

    }

    @Test
    //roll 3 swords, 4 parrots, 1 skull and score (SC 100+200+100= 400)
    public void row57(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(4,4,4,3,1,1,1,1);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 400,s.countScore(dice, 4));
        s.close();

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

    }

    @Test
    //score set of 6 monkeys and 2 skulls on first roll (SC 1100)
    public void row62(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(2,2,2,2,2,2,3,3);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 1100,s.countScore(dice, 5));
        s.close();


    }

    @Test
    //score set of 7 parrots and 1 skull on first roll (SC 2100)
    public void row63(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(1,1,1,1,1,1,1,3);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 2100,s.countScore(dice, 5));
        s.close();

    }

    @Test
    //score set of 8 coins on first roll (SC 5400)  seq of 8 + 9 coins(FC is coin) +  full chest  (no extra points for 9 coins)
    public void row64(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,0,0,0,0);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 5400,s.countScore(dice, 5));
        s.close();

    }

    @Test
    //score set of 8 coins on first roll and FC is diamond (SC 5400)
    public void row65(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,0,0,0,0,0,0);
        Assertions.assertFalse( s.isDead(dice, 5));
        assertEquals( 5400,s.countScore(dice, 5));
        s.close();

    }

    @Test
    //score set of 8 swords on first roll and FC is captain (SC 4500x2 = 9000) since full chest
    public void row66(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(4,4,4,4,4,4,4,4);
        Assertions.assertFalse( s.isDead(dice, 1));
        assertEquals( 9000,s.countScore(dice, 1));
        s.close();

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

    }

    @Test
    //get 4 monkeys, 2 coins and 2 skulls with FC coin. Score 600
    public void row72(){
        Server s = new Server(4000, false);
        ArrayList<Integer> dice = generateDice(0,0,3,3,2,2,2,2);
        Assertions.assertFalse( s.isDead(dice, 4));
        assertEquals( 600,s.countScore(dice, 4));
        s.close();
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
    }









}
