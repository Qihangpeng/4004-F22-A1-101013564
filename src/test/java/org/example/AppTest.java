package org.example;

import static org.junit.Assert.assertTrue;
import Player.Player;
import Server.Server;
import org.junit.Test;

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


}
