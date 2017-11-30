package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class LocalAlignerTest {
    private final float epsilon = 0.001f;

    @Test
    public void alignment() {
        Alphabet alphabet = new Alphabet("Alphabet", 4);
        Sequence sequenceOne = new Sequence("SequenceOne");
        Sequence sequenceTwo = new Sequence("SequenceTwo");
        LocalAlignmentMatrix matrix = new LocalAlignmentMatrix("Test",sequenceOne,sequenceTwo);
        ScoreMatrix scores = new ScoreMatrix("Scores");
        ArrayList<String> alignment;
        LocalAligner aligner = new LocalAligner(-2.0f);

        sequenceOne.addTrack(new Track(alphabet,new int [] {3, 2, 2, 2, 1, 0, 3, 2, 3}));
        sequenceTwo.addTrack(new Track(alphabet,new int [] {3, 2, 3, 2, 0, 0, 1, 3}));
        scores.setAlphabetA(alphabet);
        scores.setAlphabetB(alphabet);
        Matrix m = new Matrix(new float [] {1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f},
                                alphabet.getLength(),alphabet.getLength());
        //scores.setScores(m);
//        matrix.addSequence(sequenceOne);
//        matrix.addSequence(sequenceTwo);
        matrix.allocate();
        aligner.computeAlignment(matrix, new PositionCost(sequenceOne,sequenceTwo, m));
        alignment = matrix.getAlignment();
//
//        assertEquals(0.0f, matrix.getScore(0), epsilon);
//        assertEquals(3.0f, matrix.getScore((3 * (sequenceOne.getLength() + 1)) + sequenceOne.getLength()));
//        assertEquals(2.0f, matrix.getScore((4 * (sequenceOne.getLength() + 1)) + 4));
//        assertEquals(1.0f, matrix.getScore((sequenceTwo.getLength() * (sequenceOne.getLength() + 1)) + 1));
//        assertEquals(3.0f, matrix.getScore(), epsilon);
        assertEquals("3 9", alignment.get(0));
        assertEquals("2 8", alignment.get(1));
        assertEquals("1 7", alignment.get(2));
    }
}
