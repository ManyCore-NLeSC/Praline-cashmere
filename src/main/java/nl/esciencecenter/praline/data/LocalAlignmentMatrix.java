package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class LocalAlignmentMatrix extends  AlignmentMatrix{
    private int maxScoreIndex;

    public LocalAlignmentMatrix(String id) {
        super(id);
    }

    public float getScore() {
        return getScore(maxScoreIndex);
    }

    public void setMaxScoreItem(int index) {
        maxScoreIndex = index;
    }

    public ArrayList<String> getAlignment() {
        int row = maxScoreIndex / (getSequence(0).getLength() + 1);
        int column = maxScoreIndex % (getSequence(0).getLength() + 1);
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

        return alignment;
    }
}
