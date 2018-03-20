package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.network.WebServer;

public class Praline {

    public static void main(String [] args) throws InterruptedException {
        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        WebServer server;
        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads());

        server.run();

        // Wait until termination
        synchronized ( server) {
            server.wait();
        }



        // Clean up
        server.close();
        System.exit(0);
    }
}
