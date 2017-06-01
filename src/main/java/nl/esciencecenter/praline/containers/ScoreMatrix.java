package nl.esciencecenter.praline.containers;

import java.util.ArrayList;

public class ScoreMatrix {
    private final int MAX_SEQUENCES = 2;
    private String id;
    private ArrayList<Sequence> sequences;
    private int [] matrix;

    public ScoreMatrix(String id) {
        this.id = id;
        sequences = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Sequence getSequence(int index) {
        return sequences.get(index);
    }

    public void addSequence(Sequence sequence) {
        if ( sequences.size() < MAX_SEQUENCES ) {
            sequences.add(sequence);
        }
    }

    public void allocateMatrix() {
        int size = 1;

        for ( Sequence sequence : sequences ) {
            size *= sequence.getLength();
        }
        matrix = new int [size];
    }

    public int getElement(int index) {
        if ( (index >= 0) && (index < matrix.length) ) {
            return matrix[index];
        }
        return -1;
    }

    public void setElement(int index, int value) {
        if ( (index >= 0) && (index < matrix.length) ) {
            matrix[index] = value;
        }
    }
}
