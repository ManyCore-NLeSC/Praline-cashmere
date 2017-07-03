package nl.esciencecenter.praline.containers;

import java.util.ArrayList;

public class AlignmentMatrix {
    private final int MAX_SEQUENCES = 2;
    private String id;
    private ArrayList<Sequence> sequences;
    private float [] matrix;

    public AlignmentMatrix(String id) {
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
        matrix = new float [size];
    }

    public float getElement(int index) {
        if ( (index >= 0) && (index < matrix.length) ) {
            return matrix[index];
        }
        return -1.0f;
    }

    public void setElement(int index, float value) {
        if ( (index >= 0) && (index < matrix.length) ) {
            matrix[index] = value;
        }
    }

    public float [] getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder stringMatrix = new StringBuilder();
        for ( float item : matrix ) {
            stringMatrix.append(Float.toString(item));
            stringMatrix.append(" ");
        }
        return stringMatrix.toString();
    }
}
