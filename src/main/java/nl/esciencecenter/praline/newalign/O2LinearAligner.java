package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;

import java.util.List;

public class O2LinearAligner{
    /*
    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        float gapCostA = ((LinearGapCost)  gapCostAg).cost;
        float gapCostB = ((LinearGapCost)  gapCostBg).cost;
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        cost.set(0,0,0);
        traceback.set(0,0, Move.NIL.ordinal());
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0,gapCostA * row);
            traceback.set(row,0, Move.TOP.ordinal());
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB * col);
            traceback.set(0,col, Move.LEFT.ordinal());
        }
        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                float gapA = cost.get(row - 1,col) + gapCostA;
                float gapB = cost.get(row,col-1) + gapCostB;

                float match = cost.get(row-1,col - 1)
                        + posCosts.cost(col - 1, row-1);

                float score = match;

                Move move = Move.TOP_LEFT;

                if(gapA > score){
                    score = gapA;
                    move = Move.TOP;
                }

                if(gapB > score){
                    score = gapB;
                    move = Move.LEFT;
                }

                cost.set(row,col,score);
                traceback.set(row,col,move.ordinal());

            }

        }
        List<AlignStep> align = ReferenceO3Aligner.getTraceback(traceback, sizeB, sizeA);
        return new AlignResult(cost.get(sizeB,sizeA),align);
    }
    */
}
