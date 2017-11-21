package nl.esciencecenter.praline.data;

public class Track {
    private Alphabet alphabet;

    public Track() {
    }

    public Track(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }
}
