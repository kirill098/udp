package app;

import app.common.Support;
import app.server.ProtocolServer;

public class Server {

    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Usage: server_port client_port");
            return;
        }
        try {


            int b = Integer.parseInt(args[0]);
            int c = Integer.parseInt(args[1]);

            Thread x = new Thread(new ProtocolServer(b, c, "in2.rar"));
            x.start();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
}
