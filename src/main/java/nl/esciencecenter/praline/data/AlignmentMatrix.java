package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class AlignmentMatrix {
    private final int MAX_SEQUENCES = 2;
    private String id;
    private ArrayList<Sequence> sequences;
    private float [] scores;
    private Move [] pointers;

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
            size *= sequence.getLength() + 1;
        }
        scores = new float [size];
        pointers = new Move [size];
    }

    public float getScore(int index) {
        if ( (index >= 0) && (index < scores.length) ) {
            return scores[index];
        }
        return -1.0f;
    }

    public Move getMove(int index) {
        if ( (index >= 0) && (index < pointers.length) ) {
            return pointers[index];
        }
        return Move.NIL;
    }

    public void setScore(int index, float value) {
        if ( (index >= 0) && (index < scores.length) ) {
            scores[index] = value;
        }
    }

    public void setMove(int index, Move move) {
        if ( (index >= 0) && ( index < pointers.length) ) {
            pointers[index] = move;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringMatrix = new StringBuilder();

        for ( float item : scores ) {
            stringMatrix.append(Float.toString(item));
            stringMatrix.append(" ");
        }
        return stringMatrix.toString();
    }
}
