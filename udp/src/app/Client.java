package app;

import app.client.ProtocolClient;
import app.common.Support;


public class Client {

    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Usage: client_port server_port");
            return;
        }
        try {
            int b = Integer.parseInt(args[0]);
            int c = Integer.parseInt(args[1]);

            Thread x = new Thread(new ProtocolClient(b, c, "out.rar"));
            x.start();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
}
