package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.GlobalAlignmentMatrix;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.data.ScoreMatrix;

// Needleman–Wunsch algorithm
public class GlobalAligner extends  Aligner {

    public GlobalAligner() {
        super();
    }

    public GlobalAligner(float gapScore) {
        super(gapScore);
    }

    public void computeAlignment(GlobalAlignmentMatrix matrix, ScoreMatrix scores) {
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
                matrix.setScore((row * (matrix.getSequence(0).getLength() + 1)) + column, bestScore);
                matrix.setMove((row * (matrix.getSequence(0).getLength() + 1)) + column, bestMove);
            }
        }
    }

    private void initialize(GlobalAlignmentMatrix matrix) {
        matrix.setScore(0, 0.0f);
        matrix.setMove(0, Move.NIL);
        for ( int item = 1; item < matrix.getSequence(0).getLength() + 1; item++ ) {
            matrix.setScore(item, matrix.getScore(item - 1) + getGapScore());
            matrix.setMove(item, Move.LEFT);
        }
        for ( int item = 1; item < matrix.getSequence(1).getLength() + 1; item++) {
            matrix.setScore(item * (matrix.getSequence(0).getLength() + 1), matrix.getScore((item - 1) * (matrix.getSequence(0).getLength() + 1)) + getGapScore());
            matrix.setMove(item * (matrix.getSequence(0).getLength() + 1), Move.TOP);
        }
    }
}
