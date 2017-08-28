package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.LocalAlignmentMatrix;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.data.ScoreMatrix;

// Smith-Waterman algorithm
public class LocalAligner extends Aligner {

    public LocalAligner() {
        super();
    }

    public LocalAligner(float gapScore) {
        super(gapScore);
    }

    public void computeAlignment(LocalAlignmentMatrix matrix, ScoreMatrix scores) {
        initialize(matrix);
        for ( int row = 1; row < matrix.getSequence(1).getLength() + 1; row++ ) {
            for ( int column = 1; column < matrix.getSequence(0).getLength() + 1; column++ ) {
                float bestScore = matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + column) + getGapScore();
                Move bestMove = Move.TOP;

                if ( (matrix.getScore((row * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + getGapScore()) >= bestScore ) {
                    bestScore = matrix.getScore((row * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + getGapScore();
                    bestMove = Move.LEFT;
                }
                if ( matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + scores.getScore(matrix.getSequence(0).getElement(column - 1), matrix.getSequence(1).getElement(row - 1)) >= bestScore ) {
                    bestScore = matrix.getScore(((row - 1) * (matrix.getSequence(0).getLength() + 1)) + (column - 1)) + scores.getScore(matrix.getSequence(0).getElement(column - 1), matrix.getSequence(1).getElement(row - 1));
                    bestMove = Move.TOP_LEFT;
                }
                if ( bestScore > matrix.getScore() ) {
                    matrix.setMaxScoreItem((row * (matrix.getSequence(0).getLength() + 1)) + column);
                }
                if ( bestScore < 0 ) {
                    matrix.setScore((row * (matrix.getSequence(0).getLength() + 1)) + column, 0);
                    matrix.setMove((row * (matrix.getSequence(0).getLength() + 1)) + column, Move.NIL);
                } else {
                    matrix.setScore((row * (matrix.getSequence(0).getLength() + 1)) + column, bestScore);
                    matrix.setMove((row * (matrix.getSequence(0).getLength() + 1)) + column, bestMove);
                }
            }
        }
    }

    private void initialize(LocalAlignmentMatrix matrix) {
        matrix.setScore(0, 0.0f);
        matrix.setMove(0, Move.NIL);
        for ( int item = 1; item < matrix.getSequence(0).getLength() + 1; item++ ) {
            matrix.setScore(item, 0);
            matrix.setMove(item, Move.NIL);
        }
        for ( int item = 1; item < matrix.getSequence(1).getLength() + 1; item++) {
            matrix.setScore(item * (matrix.getSequence(0).getLength() + 1), 0);
            matrix.setMove(item * (matrix.getSequence(0).getLength() + 1), Move.NIL);
        }
    }
}
