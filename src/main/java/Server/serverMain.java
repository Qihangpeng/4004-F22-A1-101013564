package Server;

public class serverMain {
    public static void main(String[] args){
        Server s = new Server(4000, false);
        s.connect();
        s.gameStart();
    }
}
