package nl.esciencecenter.praline;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;
import nl.esciencecenter.praline.network.WebServer;

import java.util.ArrayList;
import java.util.HashMap;

public class Praline {
    // WebServer
    private static final int nrThreads = 4;
    private static WebServer server;
    // Sequences
    private static ArrayList<Sequence> sequences;
    private static HashMap<String, ScoreMatrix> scores;

    public static void main(String [] args) {
        sequences = new ArrayList<>();
        scores = new HashMap<>();

        // Initialize web server
        server = new WebServer(nrThreads);
        server.setSequences(sequences);
        server.setScores(scores);

        System.exit(0);
    }
}
