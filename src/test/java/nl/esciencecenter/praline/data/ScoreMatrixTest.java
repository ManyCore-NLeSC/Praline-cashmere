package nl.esciencecenter.praline.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreMatrixTest {
    private String matrixName = "MatrixName";
    private String alphabetName = "AlphabetName";
    private int alphabetLength = 32;

    @Test
    public void getName() {
        ScoreMatrix matrix = new ScoreMatrix(matrixName);

        assertEquals(matrixName, matrix.getName());
    }

    @Test
    public void getAlphabet() {
        Alphabet alphabet = new Alphabet(alphabetName, alphabetLength);
        ScoreMatrix matrix = new ScoreMatrix(matrixName, alphabet);

        assertEquals(alphabetName, matrix.getAlphabet().getName());
    }

    @Test
    public void setAlphabet() {
        Alphabet alphabet = new Alphabet(alphabetName, alphabetLength);
        ScoreMatrix matrix = new ScoreMatrix(matrixName);

        matrix.setAlphabet(alphabet);
        assertEquals(alphabetName, matrix.getAlphabet().getName());
    }
}
