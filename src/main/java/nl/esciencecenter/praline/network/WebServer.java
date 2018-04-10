package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.data.AlignResult;
import nl.esciencecenter.praline.data.AlignmentMode;

import java.util.HashMap;

import static spark.Spark.*;

public class WebServer {

    // Global data structures
    private final HashMap<String, Matrix2DF[]> costs;
    private final HashMap<String, Matrix2DF[]> profiles;
    private final HashMap<String, AlignResult> profileAlignments;
    private final HashMap<String, SequenceAlignmentQueue> sequenceAlignmentQueue;
    private final HashMap<String, AlignmentTree> treeQueue;

    public WebServer(int threads) {
        costs = new HashMap<>();
        profiles = new HashMap<>();
        profileAlignments = new HashMap<>();
        sequenceAlignmentQueue = new HashMap<>();
        treeQueue = new HashMap<>();
        threadPool(threads);
        init();
    }

    public void close() {
        stop();
    }
    


    public void run() {
        awaitInitialization();
        /*
         * Register a tree to process.
         */
        post("/register/tree/:name/:leaves/:cost_matrix_name/:alignment_mode/:start_gap/:extend_gap",
                (request, response) -> {
                    if ( treeQueue.containsKey(request.params(":name")) ) {
                        response.status(409);
                        return "Tree \"" + request.params(":name") + "\" already registered.";
                    }
                    int statusCode = registerTree(request.params(":name"), Integer.parseInt(request.params(":leaves")), request.body());
                    response.status(statusCode);
                    return "Tree \"" + request.params(":name") + "\" registered.";
                });
        /*
         * Register a cost matrix.
         */
        get("/register/cost_matrix/:matrix_name/:scores_number", ((request, response) -> {

            if ( profiles.containsKey(request.params(":matrix_name")) ) {
                response.status(409);
                return "Cost Matrix \"" + request.params(":matrix_name") + "\" already registered.";
            }
            int statusCode = processRegisterCostMatrix(request.params(":matrix_name"),
                    Integer.parseInt(request.params(":scores_number")));
            response.status(statusCode);
            String s =  "Cost Matrix \"" + request.params(":matrix_name") + "\" registered.";
            System.err.println(s);
            return s;
        }));
        /*
         * Register an alignment queue.
         */
        get("/register/alignment_queue/:queue_name/:cost_matrix_name/:alignment_mode/:start_gap/:extend_gap",
                (request, response) -> {
                    if ( sequenceAlignmentQueue.containsKey(request.params(":queue_name")) ) {
                        response.status(409);
                        return "Queue \"" + request.params(":queue_name") + "\" already registered.";
                    }
                    int statusCode = registerSequenceAlignmentQueue(request.params(":queue_name"),
                            request.params(":cost_matrix_name"), request.params(":alignment_mode"),
                            Float.parseFloat(request.params(":start_gap")), Float.parseFloat(request.params(":extend_gap")));
                    response.status(statusCode);
                    return "Queue \"" + request.params(":queue_name") + "\" registered.";
                });
        /*
         * Register a profile.
         */
        get("/register/profile/:profile_name/:tracks_number", ((request, response) -> {
            if ( profiles.containsKey(request.params(":profile_name")) ) {
                response.status(409);
                return "Profile \"" + request.params(":profile_name") + "\" already registered.";
            }
            int statusCode = processRegisterProfile(request.params(":profile_name"),
                    Integer.parseInt(request.params(":tracks_number")));
            response.status(statusCode);
            return "Profile \"" + request.params(":profile_name") + "\" registered.";
        }));
        /*
         * Add a track to a profile.
         */
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
        /*
         * Add a score to a cost matrix.
         */
        post("/send/cost_matrix/:matrix_name/:score_number/:score_size", (request, response) -> {

            if ( !costs.containsKey(request.params(":matrix_name")) ) {
               response.status(404);
               String s = "Cost Matrix \"" + request.params(":matrix_name") + "\" does not exist.";
               System.err.println(s);
               return s;
            }
            int statusCode = processSendCostMatrix(request.params(":matrix_name"),
                    Integer.parseInt(request.params(":score_number")),
                    Integer.parseInt(request.params(":score_size")), request.body());
            response.status(statusCode);
            return "Added score to Cost Matrix \"" + request.params(":matrix_name") + "\".";
        });
        /*
         * Add a sequence to an alignment queue.
         */
        post("/send/sequence/:length/toqueue/:queue_name", (request, response) -> {
            if ( !sequenceAlignmentQueue.containsKey(request.params(":queue_name")) ) {
                response.status(404);
                return "Queue \"" + request.params(":queue_name") + "\" does not exist.";
            }
            int statusCode = sendSequenceToQueue(Integer.parseInt(request.params(":length")), request.params(":queue_name"),
                    request.body());
            response.status(statusCode);
            return "Added sequence to queue.";
        });
        /*
         * Send a sequence to a tree.
         */
        post("/send/sequence/:length/totree/:tree_name", ((request, response) -> {
            if ( !treeQueue.containsKey(request.params(":tree_name")) ) {
                response.status(404);
                return "Tree \"" + request.params(":tree_name") + "\" does not exist.";
            }
            int statusCode = sendSequenceToTree(Integer.parseInt(request.params(":length")), request.params(":tree_name"),
                    request.body());
            response.status(statusCode);
            return "Added sequence to tree.";
        }));
        /*
         * Retrieve the alignment score matrix associated with an alignment queue.
         */
        get("/receive/score_matrix/:queue_name", (request, response) -> {
            String queueName= request.params(":queue_name");
            if ( !sequenceAlignmentQueue.containsKey(queueName) ) {
                response.status(404);
                return "Queue \"" + request.params(":queue_name") + "\" does not exist.";
            }
            SequenceAlignmentQueue queue;
            synchronized (sequenceAlignmentQueue){
                queue  = sequenceAlignmentQueue.get(queueName);

            }
            queue.waitForResult();
            String scoreMatrix = queue.getScoreMatrixString();
            response.status(200);
            return scoreMatrix;
        });
        /*
         * Retrieve the alignment score of two profiles.
         */
        get("/retrieve/score/:profile_one/:profile_two", ((request, response) -> {
            if ( !profileAlignments.containsKey(request.params(":profile_one") + "_" + request.params(":profile_two")) ) {
                response.status(404);
                return "Alignment " + request.params(":profile_one") + "_" + request.params(":profile_two")
                        + " does not exist.";
            } else {
                response.status(200);
                return profileAlignments.get(request.params(":profile_one") + "_" + request.params(":profile_two")).getScore();
            }
        }));
        /*
         * Retrieve the alignment steps of two profiles.
         */
        get("/retrieve/steps/:profile_one/:profile_two", ((request, response) -> {
            if ( !profileAlignments.containsKey(request.params(":profile_one") + "_" + request.params(":profile_two")) ) {
                response.status(404);
                return "Alignment " + request.params(":profile_one") + "_" + request.params(":profile_two")
                        + " does not exist.";
            } else {
                response.status(200);

                return profileAlignments.get(request.params(":profile_one") + "_" + request.params(":profile_two")).toString();
            }
        }));
        /*
         * Retrieve the alignment steps of a tree.
         */
        get("/retrieve/steps/:tree_name", ((request, response) -> {
            if ( !treeQueue.containsKey(request.params(":tree_name")) ) {
                response.status(404);
                return "Tree \"" + request.params(":tree_name") + "\" does not exist.";
            }
            // TODO: implement serialization of the alignment steps of a tree.
            return "";
        }));
        /*
         * Align two profiles.
         */
        get("/align/:profile_one/:profile_two/:cost_matrix/:start_gap/:extend_gap/:mode", (request, response) -> {
            if ( !profiles.containsKey(request.params(":profile_one"))
                    || !profiles.containsKey(request.params(":profile_two")) ) {
                response.status(404);
                return "The profiles do not exist.";
            }
            if ( !costs.containsKey(request.params(":cost_matrix")) ) {
                response.status(404);
                return "The cost matrix does not exist.";
            }
            AffineMotifProfileAlignInterface aligner = new AffineMotifProfileAlignInterface();
            AlignmentMode mode;
            if ( request.params(":mode").equals("global")) {
                mode = AlignmentMode.GLOBAL;
            } else if ( request.params(":mode").equals("local") ) {
                mode = AlignmentMode.LOCAL;
            } else if ( request.params(":mode").compareTo("semiglobal") == 0 ) {
                mode = AlignmentMode.SEMIGLOBAL;
            } else {
                response.status(405);
                return "Alignment mode \"" + request.params(":mode") + "\" not supported.";
            }
            profileAlignments.put(request.params(":profile_one") + "_" + request.params(":profile_two"),
                    aligner.computeAlignment(profiles.get(request.params(":profile_one")),
                            profiles.get(request.params(":profile_two")),
                            costs.get(request.params(":cost_matrix")),
                            Float.parseFloat(request.params(":start_gap")),
                            Float.parseFloat(request.params(":extend_gap")),
                            mode));
            response.status(200);
            return "Alignment processed.";
        });
        /*
         * Shut down the server.
         */
        get("/terminate", ((request, response) -> {
            synchronized ( this ) {
                this.notifyAll();
            }
            response.status(200);
            return "Server shutting down.";
        }));
        // Default routes
        get("/", ((request, response) -> halt(501)));
        post("/", ((request, response) -> halt(501)));
        // Compress responses
        after(((request, response) -> response.header("Content-Encoding", "gzip")));
    }


    // Register data structures
    private int processRegister(String name, int size, HashMap<String, Matrix2DF []> data) {
        synchronized ( data ) {
            data.put(name, new Matrix2DF [size]);
        }
        return 201;
    }

    private int processRegisterProfile(String id, int tracks) {
        return processRegister(id, tracks,  profiles);
    }

    private int processRegisterCostMatrix(String id, int length) {
        return processRegister(id, length,  costs);
    }

    private int registerSequenceAlignmentQueue(String name, String costMatrix, String alignmentMode,
                                               Float costStartGap, Float costExtendGap) {
        SequenceAlignmentQueue queue;
        AlignmentMode mode;
        if (alignmentMode.equals("global") ) {
            mode = AlignmentMode.GLOBAL;
        } else if ( alignmentMode.equals("local") ) {
            mode = AlignmentMode.LOCAL;
        } else {
            mode = AlignmentMode.SEMIGLOBAL;
        }
        queue = new SequenceAlignmentQueue(mode, costs.get(costMatrix),costStartGap, costExtendGap);
        synchronized(sequenceAlignmentQueue){
            sequenceAlignmentQueue.put(name, queue);
        }

        return 201;
    }

    private int registerTree(String name, int nrLeaves, String body) {
        synchronized ( treeQueue ) {
            //treeQueue.put(name, ReadAlignmentTree.readTree(nrLeaves, body));
        }
        return 201;
    }

    // Receive data structures
    private int processSend(String name, int position, int rows, int columns, String values,
                            HashMap<String, Matrix2DF[]> data) {
        String [] items = values.split(" ");
        float [][] initializationMatrix = new float [rows][columns];

        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                initializationMatrix[row][column] = Float.parseFloat(items[(row * columns) + column]);
            }
        }
        synchronized ( data ) {
            data.get(name)[position] = new Matrix2DF(initializationMatrix);
        }
        return 201;
    }

    private int processSendTrack(String profileID, int trackNumber, int rows, int columns, String track) {
        return processSend(profileID, trackNumber, rows, columns, track, profiles);
    }

    private int processSendCostMatrix(String matrixID, int scoreNumber, int scoreSize, String score) {
        return processSend(matrixID, scoreNumber, scoreSize, scoreSize, score,  costs);
    }

    private int [][] parseSequence(int length, String body) {
        String [] items = body.split(" ");
        int [][] sequence = new int [items.length / length][length];


        for ( int row = 0; row < items.length / length; row++ ) {
            for ( int column = 0; column < length; column++ ) {
                sequence[row][column] = Integer.parseInt(items[(row * length) + column]);
            }
        }
        return sequence;
    }

    private int sendSequenceToQueue(int length, String queueName, String body) {
        int [][] sequence = parseSequence(length, body);
        synchronized ( sequenceAlignmentQueue) {
            sequenceAlignmentQueue.get(queueName).addElement(sequence);
        }

        return 201;
    }

    private int sendSequenceToTree(int length, String queueName, String body) {
        int [][] sequence = parseSequence(length, body);
        synchronized ( treeQueue ) {
        }

        return 201;
    }
}
