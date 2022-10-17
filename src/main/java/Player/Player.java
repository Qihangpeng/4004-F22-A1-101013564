package Player;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Player {
    private DatagramSocket send;
    private DatagramSocket receive;
    private String name;
    public Player(String name, int port, InetAddress address){
        this.name = name;
        try{
            send = new DatagramSocket();
            receive = new DatagramSocket(port, address);
            System.out.println(name + " has Connected to Server");
        }catch(IOException e){
            System.out.println(e);
        }
    }
    public static void main(String[] args){

    }


}
