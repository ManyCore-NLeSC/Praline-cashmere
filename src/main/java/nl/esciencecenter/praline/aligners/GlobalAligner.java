package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignmentMatrix;
import nl.esciencecenter.praline.data.Move;

// Needleman–Wunsch algorithm
public class GlobalAligner {
    private float gapScore;

    public GlobalAligner() {
        gapScore = -2.0f;
    }

    public GlobalAligner(float gapScore) {
        this.gapScore = gapScore;
    }

    public float getGapScore() {
        return gapScore;
    }

    public void setGapScore(float gapScore) {
        this.gapScore = gapScore;
    }

    public void computeAlignment(AlignmentMatrix matrix) {
        initialize(matrix);
        for ( int row = 1; row < matrix.getSequence(1).getLength() + 1; row++ ) {
            for ( int column = 1; column < matrix.getSequence(0).getLength() + 1; column++ ) {
                float tempScore = 0.0f;
                float bestScore = 0.0f;
                Move bestMove = Move.NIL;

                if ( (matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength()) + 1) + column) + gapScore) > (matrix.getScore((row * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + gapScore) ) {
                    bestScore = matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + column) + gapScore;
                    bestMove = Move.TOP;
                } else {
                    bestScore = matrix.getScore((row * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + gapScore;
                    bestMove = Move.LEFT;
                }
                if ( matrix.getSequence(0).getElement(column) == matrix.getSequence(1).getElement(row) ) {
                    tempScore = matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + 1.0f;
                } else {
                    tempScore = matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) - 1.0f;
                }
                if ( tempScore  > bestScore ) {
                    bestScore = tempScore;
                    bestMove = Move.TOP_LEFT;
                }
                matrix.setScore((row * (matrix.getSequence(0).getLength() + 1)) + column, bestScore);
                matrix.setMove((row * (matrix.getSequence(0).getLength() + 1)) + column, bestMove);
            }
        }
    }

    private void initialize(AlignmentMatrix matrix) {
        matrix.setScore(0, 0.0f);
        matrix.setMove(0, Move.NIL);
        for ( int item = 1; item < matrix.getSequence(0).getLength() + 1; item++ ) {
            matrix.setScore(item, matrix.getScore(item - 1) + gapScore);
            matrix.setMove(item, Move.LEFT);
        }
        for ( int item = 1; item < matrix.getSequence(1).getLength() + 1; item++) {
            matrix.setScore(item * (matrix.getSequence(0).getLength() + 1), matrix.getScore((item - 1) * (matrix.getSequence(0).getLength() + 1)) + gapScore);
            matrix.setMove(item * (matrix.getSequence(0).getLength() + 1), Move.TOP);
        }
    }
}
