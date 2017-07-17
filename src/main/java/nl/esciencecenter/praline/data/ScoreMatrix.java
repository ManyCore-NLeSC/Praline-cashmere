package nl.esciencecenter.praline.data;

public class ScoreMatrix {
    private String name;
    private Alphabet alphabet;
    private float [] matrix;

    public ScoreMatrix(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public void setScores(float [] matrix) {
        if ( matrix.length == (alphabet.getLength() * alphabet.getLength()) ) {
            this.matrix = matrix;
        }
    }

    public float getScore(int i, int j) {
        return matrix[(i * alphabet.getLength()) + j];
    }
}
