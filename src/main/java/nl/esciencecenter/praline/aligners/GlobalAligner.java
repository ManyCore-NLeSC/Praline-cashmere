package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignmentMatrix;
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

    @Override
    void initMatrix(AlignmentMatrix matrix) {
        matrix.setScore(0, 0,0.0f);
        matrix.setMove(0, 0, Move.NIL);
        for ( int item = 1; item < matrix.getSeqA().getLength() + 1; item++ ) {
            matrix.setScore(item, 0, matrix.getScore(item-1,0) + getGapScore());
            matrix.setMove(item,0, Move.LEFT);
        }
        for ( int item = 1; item < matrix.getSeqB().getLength() + 1; item++) {
            matrix.setScore(0,item, matrix.getScore(0,item-1) + getGapScore());
            matrix.setMove(0,item, Move.TOP);
        }
    }

}
