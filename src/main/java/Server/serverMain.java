package Server;

import java.util.ArrayList;

public class serverMain {
    public static void main(String[] args){
        Server s = new Server(4000, false);
        s.connect();
        s.gameStart(false, new ArrayList<ArrayList<Integer>>());
    }
}
