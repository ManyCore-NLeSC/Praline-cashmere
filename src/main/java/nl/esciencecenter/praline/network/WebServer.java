package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.AlignmentMatrix;
import nl.esciencecenter.praline.containers.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import static spark.Spark.*;

public class WebServer {
    // Local data structures
    private HashSet<String> knownSequences;
    // Global data structures
    private final ReentrantLock sequenceLock;
    private ArrayList<Sequence> sequences;
    private HashMap<String, AlignmentMatrix> alignments;

    public WebServer(int threads, ReentrantLock lock) {
        sequenceLock = lock;
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
        // Send an alignment
        get("/receive/:sequence1/:sequence2", (request, response) -> {
            AlignmentMatrix alignment = alignments.get(request.params(":sequence1") + "_" + request.params(":sequence2"));
            if ( alignment != null ) {
                response.status(200);
                return alignment.toString();
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

    public void close() {
        stop();
    }

    public void setSequences(ArrayList<Sequence> sequences) {
        this.sequences = sequences;
    }

    public void setScores(HashMap<String, AlignmentMatrix> alignments) {
        this.alignments = alignments;
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
            success = sequences.add(sequence);
            if (success) {
                sequenceLock.notifyAll();
            }
        }
        if ( success ) {
            knownSequences.add(id);
            return 201;
        } else {
            return 500;
        }
    }
}
