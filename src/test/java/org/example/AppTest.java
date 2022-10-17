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
    @Test
    public void playerConnection(){
        Player a = new Player((byte) 1,4001);
        Player b = new Player((byte) 2,4002);
        Player c = new Player((byte) 3,4003);
        Thread st = new Thread(new Runnable() {
            @Override
            public void run() {
                Server s = new Server(4000);
                s.connect();
            }
        });
        st.start();
        a.connect(4000);
        b.connect(4000);
        c.connect(4000);
    }

}
