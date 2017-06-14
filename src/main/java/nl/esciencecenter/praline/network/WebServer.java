package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static spark.Spark.*;

public class WebServer {
    private Object sequenceLock;
    private HashSet<String> knownSequences;
    private ArrayList<Sequence> sequencesQueue;
    private HashMap<String, ScoreMatrix> scores;

    public WebServer(int threads) {
        knownSequences = new HashSet<>();
        threadPool(threads);
        init();
    }

    public void run() {
        awaitInitialization();
        // Receive a sequence
        post("/send/:sequence", (request, response) -> {
            if ( knownSequences.contains(request.params(":sequence")) ) {
                response.status(409);
                return "Sequence \"" + request.params(":sequence") + "\" already exists.";
            }
            int statusCode = processSendSequence(request.params(":sequence"), request.body());
            response.status(statusCode);
            return "Sequence \"" + request.params(":sequence") + "\" processed.";
        });
        // Send a score
        get("/receive/:sequence1/:sequence2", (request, response) -> {
            ScoreMatrix score = scores.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( score != null ) {
                response.status(200);
                return score.toString();
            } else {
                response.status(404);
                return "";
            }
        });
        // Default routes
        get("/", ((request, response) -> halt(501)));
        post("/", ((request, response) -> halt(501)));
        // Compress responses
        after(((request, response) -> response.header("Content-Encoding", "gzip")));
    }

    public void close() {
        stop();
    }

    public void setSequences(ArrayList<Sequence> sequencesQueue, Object lock) {
        sequenceLock = lock;
        this.sequencesQueue = sequencesQueue;
    }

    public void setScores(HashMap<String, ScoreMatrix> scores) {
        this.scores = scores;
    }

    private int processSendSequence(String id, String body) {
        boolean success;
        int iterator = 0;
        int [] elements = new int [body.split(" ").length];
        Sequence sequence = new Sequence(id);

        for ( String item : body.split(" ") ) {
            elements[iterator] = Integer.parseInt(item);
            iterator++;
        }
        sequence.setElements(elements);
        synchronized ( sequenceLock ) {
            success = sequencesQueue.add(sequence);
        }
        if ( success ) {
            knownSequences.add(id);
            return 201;
        } else {
            return 500;
        }
    }
}
