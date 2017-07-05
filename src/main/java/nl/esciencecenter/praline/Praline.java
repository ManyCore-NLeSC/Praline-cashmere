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
    // Sequences
    private static final ReentrantLock sequenceLock = new ReentrantLock();
    private volatile static ArrayList<Sequence> newSequences;
    private static ArrayList<Sequence> activeSequences;
    // Alignments
    private static HashMap<String, AlignmentMatrix> alignments;

    public static void main(String [] args) throws InterruptedException {
        newSequences = new ArrayList<>();
        activeSequences = new ArrayList<>();
        alignments = new HashMap<>();

        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads(), sequenceLock);
        server.setSequences(newSequences);
        server.setScores(alignments);
        server.run();

        // Receive sequences from network
        while ( activeSequences.size() < arguments.getNrExpectedSequences() ) {
            synchronized ( sequenceLock ) {
                sequenceLock.wait();
                activeSequences.add(newSequences.remove(newSequences.size() - 1));
            }
        }
        for ( Sequence sequence : activeSequences ) {
            System.out.print("Sequence \"" + sequence.getId() + "\": ");
            for ( Integer symbol : sequence.getElements() ) {
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        // Send alignment matrices
        for ( Sequence sequenceOne : activeSequences ) {
            for ( Sequence sequenceTwo : activeSequences ) {
                if ( sequenceOne.getId().equals(sequenceTwo.getId()) ) {
                    continue;
                }
                AlignmentMatrix alignment = new AlignmentMatrix(sequenceOne.getId() + "_" + sequenceTwo.getId());
                alignment.addSequence(sequenceOne);
                alignment.addSequence(sequenceTwo);
                alignment.allocateMatrix();
                alignments.put(alignment.getId(), alignment);
            }
        }
        Thread.sleep(60000);

        // Clean up
        server.close();
        System.exit(0);
    }
}
