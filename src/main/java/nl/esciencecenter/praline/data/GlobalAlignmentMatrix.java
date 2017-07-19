package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class GlobalAlignmentMatrix {
    private final int MAX_SEQUENCES = 2;
    private String id;
    private ArrayList<Sequence> sequences;
    private float [] scores;
    private Move [] pointers;

    public GlobalAlignmentMatrix(String id) {
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

    public void allocate() {
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
        return Float.MIN_VALUE;
    }

    public float getScore() {
        return scores[scores.length - 1];
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

    public ArrayList<String> getAlignment() {
        int row = sequences.get(1).getLength();
        int column = sequences.get(0).getLength();
        ArrayList<String> alignment = new ArrayList<>();

        while ( getMove((row * (sequences.get(0).getLength() + 1)) + column) != Move.NIL ) {
            Move move = getMove((row * (sequences.get(0).getLength() + 1)) + column);
            alignment.add(String.valueOf(row) + " " + String.valueOf(column));
            if ( move == Move.TOP ) {
                row -= 1;
            } else if ( move == Move.LEFT ) {
                column -= 1;
            } else {
                row -= 1;
                column -= 1;
            }
        }
        return alignment;
    }
}
