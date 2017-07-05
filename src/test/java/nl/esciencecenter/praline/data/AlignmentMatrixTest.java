package nl.esciencecenter.praline.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlignmentMatrixTest {
    private final float epsilon = 0.001f;
    private final String sequenceOneName = "SequenceOne";
    private final int sequenceOneLength = 12;
    private final String sequenceTwoName = "SequenceTwo";
    private final int sequenceTwoLength = 3;

    @Test
    public void getId() {
        AlignmentMatrix alignmentMatrix = new AlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);

        assertEquals(sequenceOneName + "_" + sequenceTwoName, alignmentMatrix.getId());
    }

    @Test
    public void sequence() {
        AlignmentMatrix alignmentMatrix = new AlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName);
        Sequence sequenceTwo = new Sequence(sequenceTwoName);

        alignmentMatrix.addSequence(sequenceOne);
        alignmentMatrix.addSequence(sequenceTwo);
        assertEquals(sequenceOne.getId(), alignmentMatrix.getSequence(0).getId());
        assertEquals(sequenceTwo.getId(), alignmentMatrix.getSequence(1).getId());
    }

    @Test
    public void elements() {
        AlignmentMatrix alignmentMatrix = new AlignmentMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName, sequenceOneLength);
        Sequence sequenceTwo = new Sequence(sequenceTwoName, sequenceTwoLength);

        alignmentMatrix.addSequence(sequenceOne);
        alignmentMatrix.addSequence(sequenceTwo);
        alignmentMatrix.allocateMatrix();
        alignmentMatrix.setScore((0 * sequenceTwoLength) + 2, 43.0f);
        assertEquals(43.0f, alignmentMatrix.getScore((0 * sequenceTwoLength) + 2), epsilon);
        alignmentMatrix.setScore((11 * sequenceTwoLength) + 0, 9.0f);
        assertEquals(9.0f, alignmentMatrix.getScore((11 * sequenceTwoLength) + 0), epsilon);
        assertEquals(-1.0f, alignmentMatrix.getScore(((sequenceOneLength + 1) * (sequenceTwoLength + 1)) + sequenceTwoLength + 1), epsilon);
        String alignmentMatrixString = alignmentMatrix.toString();
        assertEquals(43.0f, Float.parseFloat(alignmentMatrixString.split(" ")[(0 * sequenceTwoLength) + 2]), epsilon);
        assertEquals(9.0f, Float.parseFloat(alignmentMatrixString.split(" ")[(11 * sequenceTwoLength) + 0]), epsilon);
    }
}