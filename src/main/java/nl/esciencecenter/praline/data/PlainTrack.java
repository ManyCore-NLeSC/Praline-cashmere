package nl.esciencecenter.praline.data;

public class PlainTrack extends Track {
    private int [] values;

    public PlainTrack() {
        super();
    }

    public PlainTrack(int [] values) {
        super();
        this.values = values;
    }

    public PlainTrack(Alphabet alphabet) {
        super(alphabet);
    }

    public PlainTrack(Alphabet alphabet, int [] values) {
        super(alphabet);
        this.values = values;
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

    @Override
    public int getLength() {
        return values.length;
    }
}
