package Player;

import java.util.Scanner;

public class playerMain {
    public static void main(String[] args){
        System.out.println("Please input player id: ");
        Scanner input = new Scanner(System.in);
        int id = input.nextInt();
        Player player = new Player((byte)id);
        player.connect();
        player.play();
    }
}
