package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.Alphabet;
import nl.esciencecenter.praline.data.GlobalAlignmentMatrix;
import nl.esciencecenter.praline.data.Sequence;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

public class WebServerTest {
    private final int nrThreads = 42;
    private final float epsilon = 0.001f;
    private final String hostname = "http://localhost:4567";
    private WebServer server;
    private HashMap<String, ReentrantLock> locks;
    private HashMap<String, Sequence> sequences;
    private HashMap<String, Alphabet> alphabets;
    private HashMap<String, GlobalAlignmentMatrix> alignments;

    @Test
    public void run() throws IOException {
        server = new WebServer(nrThreads);
        locks = new HashMap<>();
        locks.put("sequence", new ReentrantLock());
        locks.put("alphabet", new ReentrantLock());
        sequences = new HashMap<>();
        alphabets = new HashMap<>();
        alignments = new HashMap<>();
        server.setLocks(locks);
        server.setSequencesContainer(sequences);
        server.setAlphabetsContainer(alphabets);
        server.setAlignmentMatricesContainer(alignments);
        server.run();

        sequences();
        alphabets();
        alignments();

        server.close();
    }

    private void sequences() throws IOException {
        int statusCode;

        // Create control sequence
        Sequence controlSequence = new Sequence("controlOne", 17);
        StringBuilder sequenceString = new StringBuilder();
        for ( int symbol = 0; symbol < controlSequence.getLength(); symbol++ ) {
            controlSequence.setElement(symbol, symbol);
            sequenceString.append(Integer.toString(symbol));
            sequenceString.append(" ");
        }
        // Send sequence to server
        URLConnection connection = new URL(hostname + "/send/sequence/" + controlSequence.getId()).openConnection();
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
            assertEquals(controlSequence.getElement(symbol), sequences.get("controlOne").getElement(symbol));
        }
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Try to send the same sequence again
        connection = new URL(hostname + "/send/sequence/" + controlSequence.getId()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        request = connection.getOutputStream();
        request.write(sequenceString.toString().getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that the request was refused
        assertEquals(409, statusCode);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
    }

    private void alphabets() throws IOException {
        int statusCode;
        Alphabet controlAlphabet = new Alphabet("control", 14);

        // Send alphabet to server
        URLConnection connection = new URL(hostname + "/send/alphabet/" + controlAlphabet.getName()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        OutputStream request = connection.getOutputStream();
        request.write(String.valueOf(controlAlphabet.getLength()).getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that sent alphabet match
        assertEquals(201, statusCode);
        assertEquals(controlAlphabet.getLength(), alphabets.get("control").getLength());
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Try to send the same alphabet again
        connection = new URL(hostname + "/send/alphabet/" + controlAlphabet.getName()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        request = connection.getOutputStream();
        request.write(String.valueOf(controlAlphabet.getLength()).getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that the request was refused
        assertEquals(409, statusCode);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
    }

    private void alignments() throws IOException {
        int statusCode;
        String temp;

        // Create control sequence
        Sequence controlSequence = new Sequence("controlOne", 17);
        // Create control alignment matrix
        GlobalAlignmentMatrix controlScore = new GlobalAlignmentMatrix("controlOne_controlTwo");
        controlScore.addSequence(controlSequence);
        controlScore.addSequence(new Sequence("controlTwo", 4));
        controlScore.allocate();
        for ( int symbolOne = 0; symbolOne < controlScore.getSequence(0).getLength(); symbolOne++ ) {
            for ( int symbolTwo = 0; symbolTwo < controlScore.getSequence(1).getLength(); symbolTwo++ ) {
                controlScore.setScore((symbolOne * controlScore.getSequence(1).getLength()) + symbolTwo, symbolTwo);
            }
        }
        alignments.put(controlScore.getId(), controlScore);
        URLConnection connection = new URL(hostname + "/receive/alignment_matrix/" + controlSequence.getId() + "/" + controlScore.getSequence(1).getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received matrix match
        int controlSymbol = 0;
        assertEquals(200, statusCode);
        for ( String symbol : responseBody.toString().split(" ")  ) {
            assertEquals(controlScore.getScore(controlSymbol), Float.parseFloat(symbol), epsilon);
            controlSymbol++;
        }
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Not existing alignment matrix
        connection = new URL(hostname + "/receive/test/wrong").openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        assertEquals(404, statusCode);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
    }
}