package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import ibis.constellation.*;
import nl.esciencecenter.praline.network.WebServer;

public class Praline {

    public static void main(String [] args) throws InterruptedException {
        try {
            // Command line arguments
            CommandLineArguments arguments = new CommandLineArguments();
            JCommander.newBuilder().addObject(arguments).build().parse(args);

            Constellation c = ConstellationFactory.createConstellation(new ConstellationConfiguration(new Context("MSA")));
            if (c.isMaster()) {

                WebServer server;
                // Initialize web server
                server = new WebServer(arguments.getNrServerThreads(),c);

                server.run();

                c.done();
                // Wait until termination
                synchronized (server) {
                    server.wait();
                }
                // Clean up
                server.close();
            } else {
                c.done();
            }


        } catch (ConstellationCreationException e) {
            e.printStackTrace();
        }




        System.exit(0);
    }
}
