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
        Server s = new Server(4000);
        Player a = new Player("a",4001, s.InetAddress());
        Player b = new Player("b",4002, s.InetAddress());
        Player c = new Player("c",4003, s.InetAddress());
    }
    
}
