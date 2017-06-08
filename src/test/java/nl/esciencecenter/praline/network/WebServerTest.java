package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.containers.ScoreMatrix;
import nl.esciencecenter.praline.containers.Sequence;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class WebServerTest {
    private final String hostname = "http://localhost:4567";
    private WebServer server;
    private ArrayList<Sequence> sequences;
    private HashMap<String, ScoreMatrix> scores;

    public WebServerTest() {
        final int nrThreads = 42;
        sequences = new ArrayList<>();
        scores = new HashMap<>();
        server = new WebServer(nrThreads);

        server.setSequences(sequences);
        server.setScores(scores);
        server.run();
    }

    @Test
    public void run() throws IOException {
        int statusCode;

        // Create control sequence
        Sequence controlSequence = new Sequence("control", 17);
        StringBuilder sequenceString = new StringBuilder();
        for ( int symbol = 0; symbol < controlSequence.getLength(); symbol++ ) {
            controlSequence.setElement(symbol, symbol);
            sequenceString.append(Integer.toString(symbol));
            sequenceString.append(" ");
        }

        // Send sequence to server
        URLConnection connection = new URL(hostname + "/send/" + controlSequence.getId()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        OutputStream request = connection.getOutputStream();
        request.write(sequenceString.toString().getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();

        // Check that sent sequence match
        assertEquals(201, statusCode);
        assertEquals(1, sequences.size());
        for ( int symbol = 0; symbol < controlSequence.getLength(); symbol++ ) {
            assertEquals(controlSequence.getElement(symbol), sequences.get(0).getElement(symbol));
        }

        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        server.close();
    }
}