package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class WebServer {
    ArrayList<Sequence> sequences;
    HashMap<String, ScoreMatrix> scores;

    public WebServer(int threads) {
        threadPool(threads);
        init();
    }

    public void run() {
        awaitInitialization();
        post("/send/:sequence", (request, response) -> {
            int statusCode = processSendSequence(request.params(":sequence"), request.body());
            response.status(statusCode);
            return "Sequence " + request.params(":sequence") + " processed.";
        });
        get("/receive/:sequence1/:sequence2", (request, response) -> {
            ScoreMatrix score = scores.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( score != null ) {
                response.status(200);
                return score;
            } else {
                response.status(404);
                return "";
            }
        });
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
        Sequence sequence;
        ArrayList<Integer> numericSequence = new ArrayList<>();

        for ( String item : body.split(" ") ) {
            numericSequence.add(Integer.parseInt(item));
        }
        sequence = new Sequence(id, numericSequence.size());
        for ( int item = 0; item < numericSequence.size(); item++ ) {
            sequence.setElement(item, numericSequence.get(item));
        }
        sequences.add(sequence);

        return 201;
    }
}
