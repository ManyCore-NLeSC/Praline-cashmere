package nl.esciencecenter.praline.data;

public class ScoreMatrix {
    private String name;
    private Alphabet alphabetA;
    private Alphabet alphabetB;
    private Matrix matrix;

    public ScoreMatrix(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Alphabet getAlphabetA() {
        return alphabetA;
    }

    public Alphabet getAlphabetB() {
        return alphabetB;
    }

    public void setAlphabetA(Alphabet alphabet) {
        this.alphabetA = alphabet;
    }

    public void setAlphabetB(Alphabet alphabet) {
        this.alphabetB = alphabet;
    }

    public void init() {
        this.matrix = new Matrix(alphabetA.getLength(), alphabetB.getLength());
    }


    public void setScores(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setScore(int i, int j, float v) {
        matrix.set(v,i,j);
    }

    public float getScore(int i, int j) {
        return matrix.get(i,j);
    }
}
