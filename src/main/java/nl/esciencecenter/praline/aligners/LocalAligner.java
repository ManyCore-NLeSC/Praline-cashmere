package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;

// Smith-Waterman algorithm
public class LocalAligner extends Aligner {

    public LocalAligner() {
        super();
    }

    public LocalAligner(float gapScore) {
        super(gapScore);
    }

    protected ScoreResult computeScore(int column, int row, AlignmentMatrix matrix, ScoreMatrix scores){
       ScoreResult best = super.computeScore(column,row,matrix,scores);
       if(best.score <= 0){
           return new ScoreResult(0,Move.NIL);
       } else {
           return best;
       }
    }


     void initMatrix(AlignmentMatrix matrix) {
        matrix.setScore(0, 0,0.0f);
        matrix.setMove(0, 0, Move.NIL);
        for ( int item = 1; item < matrix.getSeqA().getLength() + 1; item++ ) {
            matrix.setScore(item, 0, 0);
            matrix.setMove(item,0, Move.NIL);
        }
        for ( int item = 1; item < matrix.getSeqB().getLength() + 1; item++) {
            matrix.setScore(0,item, 0);
            matrix.setMove(0,item, Move.NIL);
        }
    }
}
