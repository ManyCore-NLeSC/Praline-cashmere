package nl.esciencecenter.praline.containers;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreMatrixTest {
    private final String sequenceOneName = "SequenceOne";
    private final int sequenceOneLength = 12;
    private final String sequenceTwoName = "SequenceTwo";
    private final int sequenceTwoLength = 3;

    @Test
    public void getId() {
        ScoreMatrix scoreMatrix = new ScoreMatrix(sequenceOneName + "_" + sequenceTwoName);

        assertEquals(sequenceOneName + "_" + sequenceTwoName, scoreMatrix.getId());
    }

    @Test
    public void sequence() {
        ScoreMatrix scoreMatrix = new ScoreMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName);
        Sequence sequenceTwo = new Sequence(sequenceTwoName);

        scoreMatrix.addSequence(sequenceOne);
        scoreMatrix.addSequence(sequenceTwo);
        assertEquals(sequenceOne.getId(), scoreMatrix.getSequence(0).getId());
        assertEquals(sequenceTwo.getId(), scoreMatrix.getSequence(1).getId());
    }

    @Test
    public void elements() {
        ScoreMatrix scoreMatrix = new ScoreMatrix(sequenceOneName + "_" + sequenceTwoName);
        Sequence sequenceOne = new Sequence(sequenceOneName, sequenceOneLength);
        Sequence sequenceTwo = new Sequence(sequenceTwoName, sequenceTwoLength);

        scoreMatrix.addSequence(sequenceOne);
        scoreMatrix.addSequence(sequenceTwo);
        scoreMatrix.allocateMatrix();
        scoreMatrix.setElement((0 * sequenceTwoLength) + 2, 43);
        assertEquals(43, scoreMatrix.getElement((0 * sequenceTwoLength) + 2));
        scoreMatrix.setElement((11 * sequenceTwoLength) + 0, 9);
        assertEquals(9, scoreMatrix.getElement((11 * sequenceTwoLength) + 0));
        assertEquals(43, scoreMatrix.getMatrix()[(0 * sequenceTwoLength) + 2]);
        assertEquals(9, scoreMatrix.getMatrix()[(11 * sequenceTwoLength) + 0]);
        assertEquals(-1, scoreMatrix.getElement((sequenceOneLength * sequenceTwoLength) + sequenceTwoLength));
        String scoreMatrixString = scoreMatrix.toString();
        assertEquals(43, Integer.parseInt(scoreMatrixString.split(" ")[(0 * sequenceTwoLength) + 2]));
        assertEquals(9, Integer.parseInt(scoreMatrixString.split(" ")[(11 * sequenceTwoLength) + 0]));
    }
}