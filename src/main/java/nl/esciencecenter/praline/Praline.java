package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.data.GlobalAlignmentMatrix;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Sequence;
import nl.esciencecenter.praline.integeralign.AlignResultSteps;
import nl.esciencecenter.praline.network.WebServer;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Praline {
    // WebServer
    private static WebServer server;
    // Locks
    private static final HashMap<String, ReentrantLock> locks = new HashMap<>();
    // Shared data structures
    private static final HashMap<String, Matrix2DF []> profiles = new HashMap<>();
    private static final HashMap<String, Matrix2DF []> costs = new HashMap<>();
    private static final HashMap<String, AlignResultSteps> alignments = new HashMap<>();

    public static void main(String [] args) throws InterruptedException {
        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // Locks
        locks.put("terminate", new ReentrantLock());
        locks.put("profiles", new ReentrantLock());
        locks.put("costs", new ReentrantLock());
        locks.put("alignements", new ReentrantLock());

        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads());
        server.setLocks(locks);
        server.setProfiles(profiles);
        server.setCosts(costs);
        server.setAlignments(alignments);
        server.run();

        // While until termination
        locks.get("terminate").wait();

        // Print exit message
        System.err.println("Alignments computed: " + alignments.size());

        // Clean up
        server.close();
        System.exit(0);
    }
}
