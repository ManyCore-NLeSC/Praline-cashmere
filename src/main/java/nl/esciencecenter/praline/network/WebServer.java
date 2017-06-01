package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class WebServer {
    private int nrThreads;
    ArrayList<Sequence> sequences;
    HashMap<String, ScoreMatrix> scores;

    public WebServer(int threads) {
        nrThreads = threads;
        threadPool(nrThreads);
        init();
    }

    public void run() {
        post("/send/:sequence", (request, response) -> {
            Sequence sequence = new Sequence(request.params(":sequence"), 0);
            sequences.add(sequence);
            return "OK";
        });
        get("/receive/:sequence1/:sequence2", (request, response) -> { return "Score: " + request.params(":sequence1") + " " + request.params(":sequence2");});
        after(((request, response) -> { response.header("Content-Encoding", "gzip"); }));
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
}
