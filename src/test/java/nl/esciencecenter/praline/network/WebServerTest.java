package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.*;
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
    private HashMap<String, ScoreMatrix> scoreMatrices;
    private HashMap<String, GlobalAlignmentMatrix> globalAlignments;
    private HashMap<String, LocalAlignmentMatrix> localAlignments;

    @Test
    public void run() throws IOException {
        server = new WebServer(nrThreads);
        locks = new HashMap<>();
        locks.put("sequence", new ReentrantLock());
        locks.put("alphabet", new ReentrantLock());
        locks.put("scorematrix", new ReentrantLock());
        sequences = new HashMap<>();
        alphabets = new HashMap<>();
        scoreMatrices = new HashMap<>();
        globalAlignments = new HashMap<>();
        localAlignments = new HashMap<>();
        server.setLocks(locks);
        server.setSequencesContainer(sequences);
        server.setAlphabetsContainer(alphabets);
        server.setScoreMatricesContainer(scoreMatrices);
        server.setGlobalAlignmentMatricesContainer(globalAlignments);
        server.setLocalAlignmentMatricesContaines(localAlignments);
        server.run();

        sequences();
        alphabets();
        scoreMatrices();
        alignments();
        defaults();

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

    private void scoreMatrices() throws IOException {
        int statusCode;
        Alphabet alphabet = new Alphabet("controlAlphabet", 46);

        // Create control score matrix
        ScoreMatrix globalAlignmentMatrixMatrix = new ScoreMatrix("globalAlignmentMatrixMatrix");
        globalAlignmentMatrixMatrix.setAlphabet(alphabet);
        float [] controlMatrix = new float [alphabet.getLength() * alphabet.getLength()];
        StringBuilder controlMatrixString = new StringBuilder();
        for ( int item = 0; item < controlMatrix.length; item++ ) {
            controlMatrix[item] = item;
            controlMatrixString.append(item);
            controlMatrixString.append(".0 ");
        }
        globalAlignmentMatrixMatrix.setScores(controlMatrix);
        // Send alphabet to server
        URLConnection connection = new URL(hostname + "/send/alphabet/" + alphabet.getName()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        OutputStream request = connection.getOutputStream();
        request.write(String.valueOf(alphabet.getLength()).getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        assertEquals(201, statusCode);
        ((HttpURLConnection) connection).disconnect();
        // Send score matrix to server
        connection = new URL(hostname + "/send/score/" + alphabet.getName() + "/" + globalAlignmentMatrixMatrix.getName()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        request = connection.getOutputStream();
        request.write(controlMatrixString.toString().getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that sent sequence match
        assertEquals(201, statusCode);
        for ( int row = 0; row < alphabet.getLength(); row++ ) {
            for ( int column = 0; column < alphabet.getLength(); column++ ) {
                assertEquals(globalAlignmentMatrixMatrix.getScore(row, column), scoreMatrices.get("globalAlignmentMatrixMatrix").getScore(row, column), epsilon);
            }
        }
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Try to send the same matrix again
        connection = new URL(hostname + "/send/score/" + alphabet.getName() + "/" + globalAlignmentMatrixMatrix.getName()).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        request = connection.getOutputStream();
        request.write(controlMatrixString.toString().getBytes(StandardCharsets.UTF_8.name()));
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
        Sequence controlSequenceOne = new Sequence("controlOne", 17);
        Sequence controlSequenceTwo = new Sequence("controlTwo", 4);
        // Create control alignment matrix
        GlobalAlignmentMatrix globalAlignmentMatrix = new GlobalAlignmentMatrix("controlOne_controlTwo", controlSequenceOne,controlSequenceTwo);
        LocalAlignmentMatrix localAlignmentMatrix = new LocalAlignmentMatrix("controlOne_controlTwo", controlSequenceOne,controlSequenceTwo);
        for ( int symbolOne = 0; symbolOne < globalAlignmentMatrix.getSeqA().getLength(); symbolOne++ ) {
            for ( int symbolTwo = 0; symbolTwo < globalAlignmentMatrix.getSeqB().getLength(); symbolTwo++ ) {
                globalAlignmentMatrix.setScore(symbolOne  ,symbolTwo, symbolTwo);
                localAlignmentMatrix.setScore(symbolOne, symbolTwo, symbolTwo);
            }
        }
        globalAlignments.put(globalAlignmentMatrix.getId(), globalAlignmentMatrix);
        localAlignments.put(localAlignmentMatrix.getId(), localAlignmentMatrix);
        // Receive global alignment matrix from server
        URLConnection connection = new URL(hostname + "/receive/alignment_matrix/global/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
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
            assertEquals(globalAlignmentMatrix.getScore(controlSymbol,0), Float.parseFloat(symbol), epsilon);
            controlSymbol++;
        }
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Receive the global alignment score from the server
        connection = new URL(hostname + "/receive/alignment_score/global/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received alignment score match
        assertEquals(200, statusCode);
        assertEquals(globalAlignmentMatrix.getScore(), Float.parseFloat(responseBody.toString()), epsilon);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Receive the global alignment path
        connection = new URL(hostname + "/receive/alignment_path/global/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received alignment path match
        assertEquals(200, statusCode);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Receive local alignment matrix from server
        connection = new URL(hostname + "/receive/alignment_matrix/local/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received matrix match
        controlSymbol = 0;
        assertEquals(200, statusCode);
        for ( String symbol : responseBody.toString().split(" ")  ) {
            assertEquals(globalAlignmentMatrix.getScore(controlSymbol,0), Float.parseFloat(symbol), epsilon);
            controlSymbol++;
        }
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Receive the local alignment score from the server
        connection = new URL(hostname + "/receive/alignment_score/local/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received alignment score match
        assertEquals(200, statusCode);
        assertEquals(globalAlignmentMatrix.getScore(), Float.parseFloat(responseBody.toString()), epsilon);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
        // Receive the global alignment path
        connection = new URL(hostname + "/receive/alignment_path/local/" + controlSequenceOne.getId() + "/" + controlSequenceTwo.getId()).openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        responseBody = new StringBuilder();
        while ( (temp = response.readLine()) != null ) {
            responseBody.append(temp);
        }
        response.close();
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        // Check that received alignment path match
        assertEquals(200, statusCode);
        // Cleanup
        ((HttpURLConnection) connection).disconnect();
    }

    private void defaults() throws IOException {
        int statusCode;

        // Get
        URLConnection connection = new URL(hostname + "/").openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        ((HttpURLConnection) connection).disconnect();
        assertEquals(501, statusCode);
        connection = new URL(hostname + "/receive/test/wrong").openConnection();
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        assertEquals(404, statusCode);
        ((HttpURLConnection) connection).disconnect();
        // Post
        connection = new URL(hostname + "/").openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        OutputStream request = connection.getOutputStream();
        request.write("".getBytes(StandardCharsets.UTF_8.name()));
        statusCode = ((HttpURLConnection) connection).getResponseCode();
        ((HttpURLConnection) connection).disconnect();
        assertEquals(501, statusCode);
    }
}