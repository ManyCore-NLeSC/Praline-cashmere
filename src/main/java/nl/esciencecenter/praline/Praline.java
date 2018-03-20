package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.network.SequenceAlignmentQueue;
import nl.esciencecenter.praline.network.SequenceAlignmentResults;
import nl.esciencecenter.praline.integeralign.AlignResultSteps;
import nl.esciencecenter.praline.network.WebServer;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

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
