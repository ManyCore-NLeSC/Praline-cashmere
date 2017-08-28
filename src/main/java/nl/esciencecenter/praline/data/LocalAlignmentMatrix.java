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
        ArrayList<String> alignment = new ArrayList<>();

        return alignment;
    }
}
