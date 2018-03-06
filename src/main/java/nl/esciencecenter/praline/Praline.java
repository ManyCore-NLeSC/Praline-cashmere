package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.SequenceAlignmentQueue;
import nl.esciencecenter.praline.data.SequenceAlignments;
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
    private static final HashMap<String, SequenceAlignmentQueue> sequenceAlignmentQueue = new HashMap<>();
    private static final HashMap<String, SequenceAlignments> sequenceAlignments = new HashMap<>();

    public static void main(String [] args) throws InterruptedException {
        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // Locks
        locks.put("termination", new ReentrantLock());
        locks.put("profiles", new ReentrantLock());
        locks.put("costs", new ReentrantLock());
        locks.put("alignments", new ReentrantLock());
        locks.put("sequence_alignments", new ReentrantLock());

        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads());
        server.setLocks(locks);
        server.setProfiles(profiles);
        server.setCosts(costs);
        server.setProfileAlignments(alignments);
        server.setSequenceAlignmentQueue(sequenceAlignmentQueue);
        server.setSequenceAlignments(sequenceAlignments);
        server.run();

        // While until termination
        synchronized ( locks.get("termination") ) {
            locks.get("termination").wait();
        }

        // Print exit message
        System.err.println("Alignments computed: " + alignments.size());

        // Clean up
        server.close();
        System.exit(0);
    }
}
