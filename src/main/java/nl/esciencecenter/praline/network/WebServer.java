package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class WebServer {
    private ArrayList<Sequence> sequences;
    private HashMap<String, ScoreMatrix> scores;

    public WebServer(int threads) {
        threadPool(threads);
        init();
    }

    public void run() {
        awaitInitialization();
        // Receive a sequence
        post("/send/:sequence", (request, response) -> {
            int statusCode = processSendSequence(request.params(":sequence"), request.body());
            response.status(statusCode);
            return "Sequence " + request.params(":sequence") + " processed.";
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

    public void setSequences(ArrayList<Sequence> sequences) {
        this.sequences = sequences;
    }

    public void setScores(HashMap<String, ScoreMatrix> scores) {
        this.scores = scores;
    }

    private int processSendSequence(String id, String body) {
        int iterator = 0;
        int [] elements = new int [body.split(" ").length];
        Sequence sequence = new Sequence(id);

        for ( String item : body.split(" ") ) {
            elements[iterator] = Integer.parseInt(item);
            iterator++;
        }
        sequence.setElements(elements);
        if ( sequences.add(sequence) ) {
            return 201;
        } else {
            return 500;
        }
    }
}
