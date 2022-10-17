package Server;

import java.net.*;

public class Server {
    private DatagramSocket send;
    private DatagramSocket receive;
    private int port;
    private InetAddress address;

    public Server(int port){
        this.port = port;
        try {
            this.address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try{
            send = new DatagramSocket();
            receive = new DatagramSocket(port,address);
            System.out.println("Server is running at port: " + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public InetAddress InetAddress(){
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPort(){
        return this.port;
    }

    public static void main(String[] args){

    }


}
