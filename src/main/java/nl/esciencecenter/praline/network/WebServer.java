package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.integeralign.AlignResultSteps;
import nl.esciencecenter.praline.integeralign.AlignmentMode;
import nl.esciencecenter.praline.integeralign.EasyInterface;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static spark.Spark.*;

public class WebServer {
    // Global data structures
    private HashMap<String, ReentrantLock> locks;
    private HashMap<String, Sequence> sequences;
    private HashMap<String, Alphabet> alphabets;
    private HashMap<String, Matrix2DF []> scores;
    private HashMap<String, Matrix2DF []> profiles;
    private HashMap<String, AlignResultSteps> alignments;
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

    public void setScoreMatricesContainer(HashMap<String, Matrix2DF []> scores) {
        this.scores = scores;
    }

    public void setProfiles(HashMap<String, Matrix2DF []> profiles) {
        this.profiles = profiles;
    }

    public void setAlignments(HashMap<String, AlignResultSteps> alignments) {
        this.alignments = alignments;
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
        // Register a profile
        post("/register/profile/:profile_name", ((request, response) -> {
            if ( profiles.containsKey(request.params(":profile_name")) ) {
                response.status(409);
                return "Profile \"" + request.params(":profile_name") + "\" already registered.";
            }
            int statusCode = processRegisterProfile(request.params(":profile_name"),
                    Integer.parseInt(request.body()));
            response.status(statusCode);
            return "Profile \"" + request.params(":profile_name") + "\" registered.";
        }));
        // Add a track to a profile
        post("/send/track/:profile_name/:track_number/:track_rows/:track_columns", ((request, response) -> {
            if ( !profiles.containsKey(request.params(":profile_name")) ) {
                response.status(404);
                return "Profile \"" + request.params(":profile_name") + "\" does not exist.";
            }
            int statusCode = processSendTrack(request.params(":profile_name"),
                    Integer.parseInt(request.params(":track_number")),
                    Integer.parseInt(request.params(":track_rows")),
                    Integer.parseInt(request.params(":track_columns")), request.body());
            response.status(statusCode);
            return "Added track to profile \"" + request.params(":profile_name") + "\".";
        }));
        // Register the cost matrix
        post("/register/cost_matrix/:matrix_name", ((request, response) -> {
            if ( profiles.containsKey(request.params(":matrix_name")) ) {
                response.status(409);
                return "Cost Matrix \"" + request.params(":matrix_name") + "\" already registered.";
            }
            int statusCode = processRegisterCostMatrix(request.params(":profile_name"),
                    Integer.parseInt(request.body()));
            response.status(statusCode);
            return "Cost Matrix \"" + request.params(":matrix_name") + "\" registered.";
        }));
        // Add a score to the cost matrix
        post("/send/cost_matrix/:matrix_name/:score_number/:score_size", (request, response) -> {
            if ( !scores.containsKey(request.params(":matrix_name")) ) {
               response.status(404);
                return "Cost Matrix \"" + request.params(":matrix_name") + "\" does not exist.";
            }
            int statusCode = processSendCostMatrix(request.params(":matrix_name"),
                    Integer.parseInt(request.params(":score_number")),
                    Integer.parseInt(request.params(":score_size")), request.body());
            response.status(statusCode);
            return "Added score to Cost Matrix \"" + request.params(":matrix_name") + "\".";
        });
        // Request an alignment
        get("/align/:profile_one/:profile_two/:cost_matrix/:start_gap/:extend_gap/:mode", (request, response) -> {
            if ( !profiles.containsKey(request.params(":profile_one"))
                    || !profiles.containsKey(request.params(":profile_two")) ) {
                response.status(404);
                return "The profiles do not exist.";
            }
            if ( !scores.containsKey(request.params(":cost_matrix")) ) {
                response.status(404);
                return "The cost matrix does not exist.";
            }
            EasyInterface aligner = new EasyInterface();
            if ( request.params(":mode").compareTo("GLOBAL") == 0) {
                alignments.put(request.params(":profile_one") + "_" + request.params(":profile_two"),
                        aligner.computeAlignment(profiles.get(request.params(":profile_one")),
                                profiles.get(request.params(":profile_two")),
                                scores.get(request.params(":cost_matrix")),
                                Float.parseFloat(request.params(":start_gap")),
                                Float.parseFloat(request.params(":extend_gap")),
                                AlignmentMode.GLOBAL));
            } else if ( request.params(":mode").compareTo("LOCAL") == 0 ) {
                alignments.put(request.params(":profile_one") + "_" + request.params(":profile_two"),
                        aligner.computeAlignment(profiles.get(request.params(":profile_one")),
                                profiles.get(request.params(":profile_two")),
                                scores.get(request.params(":cost_matrix")),
                                Float.parseFloat(request.params(":start_gap")),
                                Float.parseFloat(request.params(":extend_gap")),
                                AlignmentMode.LOCAL));
            } else if ( request.params(":mode").compareTo("SEMIGLOBAL") == 0 ) {
                alignments.put(request.params(":profile_one") + "_" + request.params(":profile_two"),
                        aligner.computeAlignment(profiles.get(request.params(":profile_one")),
                                profiles.get(request.params(":profile_two")),
                                scores.get(request.params(":cost_matrix")),
                                Float.parseFloat(request.params(":start_gap")),
                                Float.parseFloat(request.params(":extend_gap")),
                                AlignmentMode.SEMIGLOBAL));
            } else {
                response.status(405);
                return "Alignment mode \"" + request.params(":mode") + "\" not supported.";
            }
            response.status(200);
            return "Alignment processed.";
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

    // Register data structures
    private int processRegister(String name, int size, ReentrantLock lock, HashMap<String, Matrix2DF []> data) {
        synchronized ( lock ) {
            data.put(name, new Matrix2DF [size]);
            lock.notifyAll();
        }
        return 201;
    }
    private int processRegisterProfile(String id, int tracks) {
        return processRegister(id, tracks, locks.get("profiles"), profiles);
    }

    private int processRegisterCostMatrix(String id, int length) {
        return processRegister(id, length, locks.get("scores"), scores);
    }

    // Receive data structures
    private int processSend(String name, int position, int rows, int columns, String values, ReentrantLock lock,
                            HashMap<String, Matrix2DF []> data) {
        String [] items = values.split(" ");
        float [][] initializationMatrix = new float [rows][columns];

        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                initializationMatrix[row][column] = Float.parseFloat(items[(row * columns) + column]);
            }
        }
        synchronized ( lock ) {
            data.get(name)[position] = new Matrix2DF(initializationMatrix);
            lock.notifyAll();
        }
        return 201;
    }

    private int processSendTrack(String profileID, int trackNumber, int rows, int columns, String track) {
        return processSend(profileID, trackNumber, rows, columns, track, locks.get("profiles"), profiles);
    }

    private int processSendCostMatrix(String matrixID, int scoreNumber, int scoreSize, String score) {
        return processSend(matrixID, scoreNumber, scoreSize, scoreSize, score, locks.get("scores"), scores);
    }
}
