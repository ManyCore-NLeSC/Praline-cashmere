package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class GlobalAlignmentMatrix extends AlignmentMatrix {

    public GlobalAlignmentMatrix(String id) {
        super(id);
    }

    public float getScore() {
        return scores[scores.length - 1];
    }

    public ArrayList<String> getAlignment() {
        int row = getSequence(1).getLength();
        int column = getSequence(0).getLength();
        ArrayList<String> alignment = new ArrayList<>();

        while ( getMove((row * (getSequence(0).getLength() + 1)) + column) != Move.NIL ) {
            Move move = getMove((row * (getSequence(0).getLength() + 1)) + column);
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
        alignment.add(String.valueOf(row) + " " + String.valueOf(column));
        return alignment;
    }
}
