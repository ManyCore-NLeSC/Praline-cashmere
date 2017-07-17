package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignmentMatrix;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.data.Sequence;
import org.junit.Test;

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
        Sequence sequenceOne = new Sequence("SequenceOne");
        Sequence sequenceTwo = new Sequence("SequenceTwo");
        AlignmentMatrix matrix = new AlignmentMatrix("Test");
        GlobalAligner aligner = new GlobalAligner();

        sequenceOne.setElements(new int [] {3, 2, 2, 2, 1, 0, 3, 2, 3});
        sequenceTwo.setElements(new int [] {3, 2, 3, 2, 0, 0, 1, 3});
        matrix.addSequence(sequenceOne);
        matrix.addSequence(sequenceTwo);
        matrix.allocateMatrix();
        aligner.computeAlignment(matrix);

        assertEquals(0.0f, matrix.getScore(0), epsilon);
        assertEquals(Move.NIL, matrix.getMove(0));
        assertEquals(-18.0f, matrix.getScore(sequenceOne.getLength()), epsilon);
        assertEquals(Move.LEFT, matrix.getMove(sequenceOne.getLength()));
        assertEquals(-16.0f, matrix.getScore(sequenceTwo.getLength() * (sequenceOne.getLength() + 1)), epsilon);
        assertEquals(Move.TOP, matrix.getMove(sequenceTwo.getLength() * (sequenceOne.getLength() + 1)));
        assertEquals(0.0f, matrix.getScore((sequenceTwo.getLength() * (sequenceOne.getLength() + 1)) + sequenceOne.getLength()), epsilon);
        assertEquals(Move.TOP_LEFT, matrix.getMove((sequenceTwo.getLength() * (sequenceOne.getLength() + 1)) + sequenceOne.getLength()));
        assertEquals(-8.0f, matrix.getScore((2 * (sequenceOne.getLength() + 1)) + 7), epsilon);
        assertEquals(Move.LEFT, matrix.getMove((2 * (sequenceOne.getLength() + 1)) + 7));
        assertEquals(0.0f, matrix.getScore(), epsilon);
    }
}
