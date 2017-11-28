package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class LocalAlignmentMatrix extends  AlignmentMatrix{
    private int maxScoreIndexCol;
    private int maxScoreIndexRow;
    private float bestScore;

    public LocalAlignmentMatrix(String id, Sequence seqA, Sequence seqB) {
        super(id,seqA,seqB);
        bestScore = Float.MIN_VALUE;
        maxScoreIndexRow = maxScoreIndexCol = 0;
    }

    public float getScore() {
        return getScore(maxScoreIndexCol, maxScoreIndexRow);
    }

    public void setScore(int col, int row, float value) {
        if(isInside(col,row)){
            if(value > bestScore ){
                maxScoreIndexCol = col;
                maxScoreIndexRow = row;
                bestScore = value;
            }
            super.setScore(col,row,value);
        }
    }

    public ArrayList<String> getAlignment(){
        return getAlignment(maxScoreIndexCol,maxScoreIndexRow);
    }




}
