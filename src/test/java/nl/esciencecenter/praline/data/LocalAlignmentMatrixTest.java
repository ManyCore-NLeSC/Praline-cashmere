package nl.esciencecenter.praline.data;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class LocalAlignmentMatrixTest {
    private final float epsilon = 0.001f;
    private final String sequenceOneName = "SequenceOne";
    private final int sequenceOneLength = 12;
    private final String sequenceTwoName = "SequenceTwo";
    private final int sequenceTwoLength = 3;

    @Test
    public void getScore() {
        LocalAlignmentMatrix localAlignmentMatrix = new LocalAlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName, sequenceOneLength);
        Sequence sequenceTwo = new Sequence(sequenceTwoName, sequenceTwoLength);

        localAlignmentMatrix.addSequence(sequenceOne);
        localAlignmentMatrix.addSequence(sequenceTwo);
        localAlignmentMatrix.allocate();
        localAlignmentMatrix.setScore(42, 15.4f);
        localAlignmentMatrix.setMaxScoreItem(42);
        assertEquals(15.4f, localAlignmentMatrix.getScore(), epsilon);
    }

    @Test
    public void getAlignment() {
        LocalAlignmentMatrix localAlignmentMatrix = new LocalAlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName, sequenceOneLength);
        Sequence sequenceTwo = new Sequence(sequenceTwoName, sequenceTwoLength);
        ArrayList<String> alignment;

        localAlignmentMatrix.addSequence(sequenceOne);
        localAlignmentMatrix.addSequence(sequenceTwo);
        localAlignmentMatrix.allocate();
        localAlignmentMatrix.setMove(47, Move.TOP_LEFT);
        localAlignmentMatrix.setMove(33, Move.TOP);
        localAlignmentMatrix.setMove(20, Move.LEFT);
        localAlignmentMatrix.setMove(19, Move.NIL);
        localAlignmentMatrix.setMaxScoreItem(47);
        alignment = localAlignmentMatrix.getAlignment();
        assertEquals("3 8", alignment.get(0));
        assertEquals("2 7", alignment.get(1));
        assertEquals("1 7", alignment.get(2));
    }
}
