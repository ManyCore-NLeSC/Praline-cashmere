package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;

import java.util.List;
import java.util.Queue;

abstract class Aligner {
    private float gapScore;

    Aligner() {
        gapScore = -2.0f;
    }

    Aligner(float gapScore) {
        this.gapScore = gapScore;
    }

    float getGapScore() {
        return gapScore;
    }

    void setGapScore(float gapScore) {
        this.gapScore = gapScore;
    }

    public void computeAlignment(AlignmentMatrix matrix, PositionCost positionCost) {
        matrix.allocate();
        initMatrix(matrix);
        for ( int row = 1; row < matrix.getSeqB().getLength() + 1; row++ ) {
            for ( int column = 1; column < matrix.getSeqA().getLength() + 1; column++ ) {
                ScoreResult res = computeScore(column, row, matrix,positionCost);

                matrix.setScore(column,row, res.score);
                matrix.setMove(column, row, res.move);
            }
        }
    }

    protected ScoreResult computeScore(int column, int row, AlignmentMatrix matrix,PositionCost positionCost){
        ScoreResult best = new ScoreResult(matrix.getScore(column, row -1) + getGapScore(), Move.TOP);
        float gap2 = matrix.getScore(column-1, row) + getGapScore();
        if ( gap2 >= best.score) {
            best = new ScoreResult(gap2,Move.LEFT);
        }
        float align = matrix.getScore(column -1, row -1) +
                positionCost.cost(column-1,row-1);

        if (  align >= best.score ) {
            best = new ScoreResult(align,Move.TOP_LEFT);
        }
        return best;
    }

    abstract void initMatrix(AlignmentMatrix m) ;
}
