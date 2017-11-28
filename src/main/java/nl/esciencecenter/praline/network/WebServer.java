package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.*;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static spark.Spark.*;

public class WebServer {
    // Global data structures
    private HashMap<String, ReentrantLock> locks;
    private HashMap<String, Sequence> sequences;
    private HashMap<String, Alphabet> alphabets;
    private HashMap<String, ScoreMatrix> scores;
    private HashMap<String, GlobalAlignmentMatrix> globalAlignments;
    private HashMap<String, LocalAlignmentMatrix> localAlignments;

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

    public void setGlobalAlignmentMatricesContainer(HashMap<String, GlobalAlignmentMatrix> globalAlignments) {
        this.globalAlignments = globalAlignments;
    }

    public void setLocalAlignmentMatricesContaines(HashMap<String, LocalAlignmentMatrix> localAlignments) {
        this.localAlignments = localAlignments;
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
        post("/send/score/:alphabet/:scorematrix", (request, response) -> {
            if ( scores.containsKey(request.params(":scorematrix")) ) {
                response.status(409);
                return "Score matrix \"" + request.params(":scorematrix") + "\" already exists.";
            }
            int statusCode = processSendScoreMatrix(request.params(":alphabet"), request.params(":scorematrix"), request.body());
            response.status(statusCode);
            return "Score matrix \"" + request.params(":scorematrix") + "\" processed.";
        });
        // Send a global alignment matrix
        get("/receive/alignment_matrix/global/:sequence1/:sequence2", (request, response) -> {
            GlobalAlignmentMatrix alignment = globalAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.toString();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send a global alignment score
        get("/receive/alignment_score/global/:sequence1/:sequence2", (request, response) -> {
            GlobalAlignmentMatrix alignment = globalAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.getScore();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send a global alignment path
        get("/receive/alignment_path/global/:sequence1/:sequence2", (request, response) -> {
           GlobalAlignmentMatrix alignment = globalAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.getAlignment().toString();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send a local alignment matrix
        get("/receive/alignment_matrix/local/:sequence1/:sequence2", (request, response) -> {
            LocalAlignmentMatrix alignment = localAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.toString();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send a local alignment score
        get("/receive/alignment_score/local/:sequence1/:sequence2", (request, response) -> {
            LocalAlignmentMatrix alignment = localAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.getScore();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Send a local alignment path
        get("/receive/alignment_path/local/:sequence1/:sequence2", (request, response) -> {
            LocalAlignmentMatrix alignment = localAlignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.getAlignment().toString();
            } else {
                response.status(404);
                return "No alignment for \""+ request.params(":sequence1") + "\" and \" " + request.params(":sequence2") + "\".";
            }
        });
        // Default routes
        get("/", ((request, response) -> halt(501)));
        post("/", ((request, response) -> halt(501)));
        // Compress responses
        after(((request, response) -> response.header("Content-Encoding", "gzip")));
    }

    private int processSendSequence(String id, String body) {
//        int iterator = 0;
//        int [] elements = new int [body.split(" ").length];
//        Sequence sequence = new Sequence(id);
//
//        for ( String item : body.split(" ") ) {
//            elements[iterator] = Integer.parseInt(item);
//            iterator++;
//        }
//        sequence.setElements(elements);
//        synchronized ( locks.get("sequence") ) {
//            sequences.put(id, sequence);
//            locks.get("sequence").notifyAll();
//        }
        //return 201;
        return 401;

    }

    private int processSendAlphabet(String id, int length) {
        Alphabet alphabet = new Alphabet(id, length);

        synchronized ( locks.get("alphabet") ) {
            alphabets.put(id, alphabet);
            locks.get("alphabet").notifyAll();
        }
        return 201;
    }

    private int processSendScoreMatrix(String alphabetId, String scoreMatrixId, String body) {
        int iterator = 0;
        float [] elements = new float [body.split(" ").length];
        ScoreMatrix score = new ScoreMatrix(scoreMatrixId);

        score.setAlphabet(alphabets.get(alphabetId));
        for ( String item : body.split(" ") ) {
            elements[iterator] = Float.parseFloat(item);
            iterator++;
        }
        score.setScores(elements);
        synchronized ( locks.get("scorematrix") ) {
            scores.put(scoreMatrixId, score);
            locks.get("scorematrix").notifyAll();
        }
        return 201;
    }
}
