package nl.esciencecenter.praline.data;

public class ScoreMatrix {
    private String name;
    private Alphabet alphabet;
    private float [] matrix;

    public ScoreMatrix(String name) {
        this.name = name;
    }

    public ScoreMatrix(String name, Alphabet alphabet) {
        this.name = name;
        this.alphabet = alphabet;
        matrix = new float [alphabet.getLength() * alphabet.getLength()];
    }

    public String getName() {
        return name;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
        matrix = new float [alphabet.getLength() * alphabet.getLength()];
    }

    public float [] getMatrix() {
        return matrix;
    }

    public void setMatrix(float [] matrix) {
        this.matrix = matrix;
    }

    public float getScore(int i, int j) {
        return matrix[(i * alphabet.getLength()) + j];
    }
}
