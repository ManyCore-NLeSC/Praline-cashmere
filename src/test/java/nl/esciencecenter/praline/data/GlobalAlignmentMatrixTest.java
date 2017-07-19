package nl.esciencecenter.praline.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class GlobalAlignmentMatrixTest {
    private final float epsilon = 0.001f;
    private final String sequenceOneName = "SequenceOne";
    private final int sequenceOneLength = 12;
    private final String sequenceTwoName = "SequenceTwo";
    private final int sequenceTwoLength = 3;

    @Test
    public void getId() {
        GlobalAlignmentMatrix globalAlignmentMatrix = new GlobalAlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);

        assertEquals(sequenceOneName + "_" + sequenceTwoName, globalAlignmentMatrix.getId());
    }

    @Test
    public void sequence() {
        GlobalAlignmentMatrix globalAlignmentMatrix = new GlobalAlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName);
        Sequence sequenceTwo = new Sequence(sequenceTwoName);

        globalAlignmentMatrix.addSequence(sequenceOne);
        globalAlignmentMatrix.addSequence(sequenceTwo);
        assertEquals(sequenceOne.getId(), globalAlignmentMatrix.getSequence(0).getId());
        assertEquals(sequenceTwo.getId(), globalAlignmentMatrix.getSequence(1).getId());
    }

    @Test
    public void elements() {
        GlobalAlignmentMatrix globalAlignmentMatrix = new GlobalAlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName, sequenceOneLength);
        Sequence sequenceTwo = new Sequence(sequenceTwoName, sequenceTwoLength);

        globalAlignmentMatrix.addSequence(sequenceOne);
        globalAlignmentMatrix.addSequence(sequenceTwo);
        globalAlignmentMatrix.allocate();
        globalAlignmentMatrix.setScore((0 * sequenceTwoLength) + 2, 43.0f);
        assertEquals(43.0f, globalAlignmentMatrix.getScore((0 * sequenceTwoLength) + 2), epsilon);
        globalAlignmentMatrix.setScore((11 * sequenceTwoLength) + 0, 9.0f);
        assertEquals(9.0f, globalAlignmentMatrix.getScore((11 * sequenceTwoLength) + 0), epsilon);
        assertEquals(-1.0f, globalAlignmentMatrix.getScore(((sequenceOneLength + 1) * (sequenceTwoLength + 1)) + sequenceTwoLength + 1), epsilon);
        String alignmentMatrixString = globalAlignmentMatrix.toString();
        assertEquals(43.0f, Float.parseFloat(alignmentMatrixString.split(" ")[(0 * sequenceTwoLength) + 2]), epsilon);
        assertEquals(9.0f, Float.parseFloat(alignmentMatrixString.split(" ")[(11 * sequenceTwoLength) + 0]), epsilon);
    }
}