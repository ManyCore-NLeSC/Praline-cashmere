package nl.esciencecenter.praline.data;

public class Sequence {
    private String id;
    private int [] sequence;

    public Sequence(String id) {
        this.id = id;
    }

    public Sequence(String id, int length) {
        this.id = id;
        sequence = new int [length];
    }

    public Sequence(String id, int [] sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    public String getId() {
        return id;
    }

    public int getLength() {
        return sequence.length;
    }

    public int getElement(int index) {
        if ( (index >= 0) && (index < sequence.length) ) {
            return sequence[index];
        }
        return -1;
    }

    public void setElement(int index, int value) {
        if ( (index >= 0) && (index < sequence.length) ) {
            sequence[index] = value;
        }
    }

    public int [] getElements() {
        return sequence;
    }

    public void setElements(int [] elements) {
        sequence = elements;
    }
}
