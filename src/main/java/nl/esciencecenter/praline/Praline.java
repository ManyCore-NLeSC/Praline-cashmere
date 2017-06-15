package nl.esciencecenter.praline;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;
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
    // Scores
    private static HashMap<String, ScoreMatrix> scores;

    public static void main(String [] args) throws InterruptedException {
        newSequences = new ArrayList<>();
        activeSequences = new ArrayList<>();
        scores = new HashMap<>();

        // Command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // Initialize web server
        server = new WebServer(arguments.getNrServerThreads(), sequenceLock);
        server.setSequences(newSequences);
        server.setScores(scores);
        server.run();

        // Receive sequences from network
        while ( activeSequences.size() < arguments.getNrExpectedSequences() ) {
            synchronized ( sequenceLock ) {
                sequenceLock.wait();
                Praline.consumeSequence(newSequences.size() - 1);
            }
        }
        for ( Sequence sequence : activeSequences ) {
            System.out.print("Sequence \"" + sequence.getId() + "\": ");
            for ( Integer symbol : sequence.getElements() ) {
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        // Send score matrices
        for ( Sequence sequenceOne : activeSequences ) {
            for ( Sequence sequenceTwo : activeSequences ) {
                if ( sequenceOne.getId().equals(sequenceTwo.getId()) ) {
                    continue;
                }
                ScoreMatrix score = new ScoreMatrix(sequenceOne.getId() + "_" + sequenceTwo.getId());
                score.addSequence(sequenceOne);
                score.addSequence(sequenceTwo);
                score.allocateMatrix();
                scores.put(score.getId(), score);
            }
        }
        Thread.sleep(60000);

        // Clean up
        server.close();
        System.exit(0);
    }

    private static void consumeSequence(int sequenceIndex) {
        synchronized ( sequenceLock ) {
            activeSequences.add(newSequences.remove(sequenceIndex));
        }
    }
}
