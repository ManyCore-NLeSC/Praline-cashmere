package nl.esciencecenter.praline.data;

public class Track {
    private Alphabet alphabet;
    private int [] values;

    public Track(int [] values) {
        this.values = values;
    }


    public Track(Alphabet alphabet, int [] values) {
        this(alphabet);
        this.values = values;
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


    public int [] getValues() {
        return values;
    }

    public void setValues(int [] values) {
        this.values = values;
    }

    public int getValue(int index) {
        return values[index];
    }

    public void setValue(int index, int value) {
        values[index] = value;
    }

    public int getLength() {
        return values.length;
    }
}
