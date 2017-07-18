package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.data.AlignmentMatrix;
import nl.esciencecenter.praline.data.Sequence;
import nl.esciencecenter.praline.network.WebServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Praline {
    // WebServer
    private static WebServer server;
    // Locks
    private static final HashMap<String, ReentrantLock> locks = new HashMap<>();
    // Sequences
    private volatile static HashMap<String, Sequence> sequences;
    // Alignments
    private volatile static HashMap<String, AlignmentMatrix> alignments;

    public static void main(String [] args) throws InterruptedException {
        sequences = new HashMap<>();
        alignments = new HashMap<>();

        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads());
        server.setSequencesContainer(sequences);
        server.setAlignmentMatricesContainer(alignments);
        server.run();

        // Locks
        locks.put("sequence", new ReentrantLock());
        locks.put("alphabet", new ReentrantLock());

        // Receive sequences from network
        while ( sequences.size() < arguments.getNrExpectedSequences() ) {
            synchronized ( locks.get("sequence") ) {
                locks.get("sequence").wait();
            }
        }
        for ( Sequence sequence : sequences.values() ) {
            System.out.print("Sequence \"" + sequence.getId() + "\": ");
            for ( Integer symbol : sequence.getElements() ) {
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        // Send alignment matrices
        for ( Sequence sequenceOne : sequences.values() ) {
            for ( Sequence sequenceTwo : sequences.values() ) {
                if ( sequenceOne.getId().equals(sequenceTwo.getId()) ) {
                    continue;
                }
                AlignmentMatrix alignment = new AlignmentMatrix(sequenceOne.getId() + "_" + sequenceTwo.getId());
                alignment.addSequence(sequenceOne);
                alignment.addSequence(sequenceTwo);
                alignment.allocate();
                alignments.put(alignment.getId(), alignment);
            }
        }
        Thread.sleep(60000);

        // Clean up
        server.close();
        System.exit(0);
    }
}
