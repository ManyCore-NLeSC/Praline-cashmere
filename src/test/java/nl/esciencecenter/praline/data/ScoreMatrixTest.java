package nl.esciencecenter.praline.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreMatrixTest {
    private final float epsilon = 0.001f;
    private String matrixName = "MatrixName";
    private String alphabetName = "AlphabetName";
    private int alphabetLength = 32;

    @Test
    public void getName() {
        ScoreMatrix matrix = new ScoreMatrix(matrixName);

        assertEquals(matrixName, matrix.getName());
    }

    @Test
    public void alphabet() {
        Alphabet alphabet = new Alphabet(alphabetName, alphabetLength);
        ScoreMatrix matrix = new ScoreMatrix(matrixName);

        matrix.setAlphabetA(alphabet);
        matrix.setAlphabetB(alphabet);
        assertEquals(alphabetName, matrix.getAlphabetA().getName());
        assertEquals(alphabetName, matrix.getAlphabetB().getName());
    }

    @Test
    public void scores() {
        Alphabet alphabet = new Alphabet(alphabetName, alphabetLength);
        ScoreMatrix matrix = new ScoreMatrix(matrixName);
        float [] scores = new float [alphabetLength * alphabetLength];

        matrix.setAlphabetA(alphabet);
        matrix.setAlphabetB(alphabet);
        Matrix m = new Matrix(alphabetLength,alphabetLength);
        m.setMatrix(scores);
        matrix.setScores(m);
        scores[0] = 1.0f;
        scores[12] = -3.0f;
        scores[932] = 92.3f;
        scores[1023] = 42.0f;
        assertEquals(1.0f, matrix.getScore(0, 0), epsilon);
        assertEquals(-3.0f, matrix.getScore(0, 12), epsilon);
        assertEquals(92.3f, matrix.getScore(29, 4), epsilon);
        assertEquals(42.0f, matrix.getScore(31, 31), epsilon);
    }
}
