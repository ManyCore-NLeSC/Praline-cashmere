package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignmentMatrix;
import nl.esciencecenter.praline.data.Move;

// Needleman–Wunsch algorithm
public class GlobalAligner {
    private float gapScore;
    private AlignmentMatrix matrix;

    public GlobalAligner() {
        gapScore = -2.0f;
    }

    public GlobalAligner(float gapScore) {
        this.gapScore = gapScore;
    }

    public GlobalAligner(AlignmentMatrix matrix) {
        this.matrix = matrix;
    }

    public GlobalAligner(float gapScore, AlignmentMatrix matrix) {
        this.gapScore = gapScore;
        this.matrix = matrix;
    }

    public float getGapScore() {
        return gapScore;
    }

    public void setGapScore(float gapScore) {
        this.gapScore = gapScore;
    }

    public void setMatrix(AlignmentMatrix matrix) {
        this.matrix = matrix;
    }

    public void initializeMatrix() {
        matrix.setScore(0, 0.0f);
        for ( int item = 1; item < matrix.getSequence(0).getLength() + 1; item++ ) {
            matrix.setScore(item, matrix.getScore(item - 1) + gapScore);
        }
        for ( int item = 1; item < matrix.getSequence(1).getLength() + 1; item++) {
            matrix.setScore(item, matrix.getScore(item * matrix.getSequence(0).getLength()) + gapScore);
        }
    }

    public void computeAlignment() {
        for ( int row = 1; row < matrix.getSequence(1).getLength() + 1; row++ ) {
            for ( int column = 1; column < matrix.getSequence(0).getLength() + 1; column++ ) {
                float tempScore = 0.0f;
                float bestScore = 0.0f;
                Move bestMove = Move.NIL;

                if ( (matrix.getScore(((row - 1) * matrix.getSequence(0).getLength()) + column) + gapScore) > (matrix.getScore((row * matrix.getSequence(0).getLength()) + (column - 1)) + gapScore) ) {
                    bestScore = matrix.getScore(((row - 1) * matrix.getSequence(0).getLength()) + column) + gapScore;
                    bestMove = Move.TOP;
                } else {
                    bestScore = matrix.getScore((row * matrix.getSequence(0).getLength()) + (column - 1)) + gapScore;
                    bestMove = Move.LEFT;
                }
                if ( matrix.getSequence(0).getElement(column) == matrix.getSequence(1).getElement(row) ) {
                    tempScore = matrix.getScore(((row - 1) * matrix.getSequence(0).getLength()) + (column - 1)) + 1.0f;
                } else {
                    tempScore = matrix.getScore(((row - 1) * matrix.getSequence(0).getLength()) + (column - 1)) - 1.0f;
                }
                if ( tempScore  > bestScore ) {
                    bestScore = tempScore;
                    bestMove = Move.TOP_LEFT;
                }
                matrix.setScore((row * matrix.getSequence(0).getLength()) + column, bestScore);
                matrix.setMove((row * matrix.getSequence(0).getLength()) + column, bestMove);
            }
        }
    }
}
