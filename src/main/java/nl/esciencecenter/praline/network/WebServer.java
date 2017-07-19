package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.GlobalAlignmentMatrix;
import nl.esciencecenter.praline.data.Alphabet;
import nl.esciencecenter.praline.data.ScoreMatrix;
import nl.esciencecenter.praline.data.Sequence;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static spark.Spark.*;

public class WebServer {
    // Global data structures
    private HashMap<String, ReentrantLock> locks;
    private HashMap<String, Sequence> sequences;
    private HashMap<String, Alphabet> alphabets;
    private HashMap<String, ScoreMatrix> scores;
    private HashMap<String, GlobalAlignmentMatrix> alignments;

    public WebServer(int threads) {
        threadPool(threads);
        init();
    }

    public void close() {
        stop();
    }

    public void setLocks(HashMap<String, ReentrantLock> locks) {
        this.locks = locks;
    }

    public void setSequencesContainer(HashMap<String, Sequence> sequences) {
        this.sequences = sequences;
    }

    public void setAlphabetsContainer(HashMap<String, Alphabet> alphabets) {
        this.alphabets = alphabets;
    }

    public void setScoreMatricesContainer(HashMap<String, ScoreMatrix> scores) {
        this.scores = scores;
    }

    public void setAlignmentMatricesContainer(HashMap<String, GlobalAlignmentMatrix> alignments) {
        this.alignments = alignments;
    }

    public void run() {
        awaitInitialization();
        // Receive a sequence
        post("/send/sequence/:sequence", (request, response) -> {
            if ( sequences.containsKey(request.params(":sequence")) ) {
                response.status(409);
                return "Sequence \"" + request.params(":sequence") + "\" already exists.";
            }
            int statusCode = processSendSequence(request.params(":sequence"), request.body());
            response.status(statusCode);
            return "Sequence \"" + request.params(":sequence") + "\" processed.";
        });
        // Receive an alphabet
        post("/send/alphabet/:alphabet", (request, response) -> {
            if ( alphabets.containsKey(request.params(":alphabet")) ) {
                response.status(409);
                return "Alphabet \"" + request.params(":alphabet") + "\" already exists.";
            }
            int statusCode = processSendAlphabet(request.params(":alphabet"), Integer.parseInt(request.body()));
            response.status(statusCode);
            return "Alphabet \"" + request.params(":alphabet") + "\" processed.";
        });
        // Receive a score matrix
        post("/send/score/:alphabet", (request, response) -> {
            if ( scores.containsKey(request.params(":alphabet")) ) {
                response.status(409);
                return "Score matrix for alphabet \"" + request.params(":alphabet") + "\" already exists.";
            }
            int statusCode = processSendScoreMatrix(request.params(":alphabet"), request.body());
            response.status(statusCode);
            return "Score matrix for alphabet \"" + request.params(":alphabet") + "\" processed.";
        });
        // Send an alignment matrix
        get("/receive/alignment_matrix/:sequence1/:sequence2", (request, response) -> {
            GlobalAlignmentMatrix alignment = alignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.toString();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send an alignment score
        get("/receive/alignment_score/:sequence1/:sequence2", (request, response) -> {
            GlobalAlignmentMatrix alignment = alignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.getScore();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send an alignment path
        // Default routes
        get("/", ((request, response) -> halt(501)));
        post("/", ((request, response) -> halt(501)));
        // Compress responses
        after(((request, response) -> response.header("Content-Encoding", "gzip")));
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
        synchronized ( locks.get("sequence") ) {
            sequences.put(id, sequence);
            locks.get("sequence").notifyAll();
        }
        return 201;
    }

    private int processSendAlphabet(String id, int length) {
        Alphabet alphabet = new Alphabet(id, length);

        synchronized ( locks.get("alphabet") ) {
            alphabets.put(id, alphabet);
            locks.get("alphabet").notifyAll();
        }
        return 201;
    }

    private int processSendScoreMatrix(String id, String body) {
        int iterator = 0;
        float [] elements = new float [body.split(" ").length];
        ScoreMatrix score = new ScoreMatrix(id);

        score.setAlphabet(alphabets.get(id));
        for ( String item : body.split(" ") ) {
            elements[iterator] = Float.parseFloat(item);
            iterator++;
        }
        score.setScores(elements);
        synchronized ( locks.get("scorematrix") ) {
            scores.put(id, score);
            locks.get("scorematrix").notifyAll();
        }
        return 201;
    }
}
