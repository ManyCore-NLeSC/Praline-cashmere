package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GlobalAlignerTest {
    private final float epsilon = 0.001f;

    @Test
    public void getGapScore() {
        GlobalAligner aligner = new GlobalAligner();
        assertEquals(-2.0f, aligner.getGapScore(), epsilon);
        aligner.setGapScore(-42.3f);
        assertEquals(-42.3f, aligner.getGapScore(), epsilon);
    }

    @Test
    public void alignment() {
        Alphabet alphabet = new Alphabet("Alphabet", 4);
        Sequence sequenceOne = new Sequence("SequenceOne");
        sequenceOne.addTrack(new Track(alphabet,new int [] {3, 2, 2, 2, 1, 0, 3, 2, 3}));

        Sequence sequenceTwo = new Sequence("SequenceTwo");
        sequenceTwo.addTrack(new Track(alphabet, new int [] {3, 2, 3, 2, 0, 0, 1, 3}));
        GlobalAlignmentMatrix matrix = new GlobalAlignmentMatrix("Test",sequenceOne,sequenceTwo);
        ScoreMatrix scores = new ScoreMatrix("Scores");
        ArrayList<String> alignment;
        GlobalAligner aligner = new GlobalAligner(-2.0f);

        scores.setAlphabet(alphabet);
        scores.setScores(new float [] {1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f});

        aligner.computeAlignment(matrix, scores);

        alignment = matrix.getAlignment();

        assertEquals(0.0f, matrix.getScore(0,0), epsilon);
        assertEquals(Move.NIL, matrix.getMove(0,0));
//        assertEquals(-18.0f, matrix.getScore(sequenceOne.getLength(),0), epsilon);
//        assertEquals(Move.LEFT, matrix.getMove(sequenceOne.getLength(),0));
//        assertEquals(-16.0f, matrix.getScore(sequenceOne.getLength(), sequenceTwo.getLength()), epsilon);
//        assertEquals(Move.TOP, matrix.getMove(sequenceTwo.getLength() * (sequenceOne.getLength() + 1)));
//        assertEquals(0.0f, matrix.getScore((sequenceTwo.getLength() * (sequenceOne.getLength() + 1)) + sequenceOne.getLength()), epsilon);
//        assertEquals(Move.TOP_LEFT, matrix.getMove((sequenceTwo.getLength() * (sequenceOne.getLength() + 1)) + sequenceOne.getLength()));
//        assertEquals(-8.0f, matrix.getScore((2 * (sequenceOne.getLength() + 1)) + 7), epsilon);
//        assertEquals(Move.LEFT, matrix.getMove((2 * (sequenceOne.getLength() + 1)) + 7));
//        assertEquals(0.0f, matrix.getScore(), epsilon);
        assertEquals("8 9", alignment.get(0));
        assertEquals("4 5", alignment.get(4));
        assertEquals("1 1", alignment.get(8));
    }
}
