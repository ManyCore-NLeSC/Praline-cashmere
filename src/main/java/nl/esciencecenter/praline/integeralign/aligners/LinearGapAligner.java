package nl.esciencecenter.praline.integeralign.aligners;

import nl.esciencecenter.praline.integeralign.Alignment;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.integeralign.AlignResult;
import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.integeralign.gapcost.IGapCost;
import nl.esciencecenter.praline.integeralign.gapcost.LinearGapCost;
import nl.esciencecenter.praline.integeralign.positioncost.IPositionCost;

import static nl.esciencecenter.praline.integeralign.aligners.ReferenceAligner.*;
public class LinearGapAligner implements IAlign {

    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts, AlignmentMode mode) {
        int gapCostA = ((LinearGapCost)  gapCostAg).cost;
        int gapCostB = ((LinearGapCost)  gapCostBg).cost;
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        cost.set(0,0,0);
        traceback.set(0,0, Move.NIL.ordinal());
        int bestRow = 0;
        int bestCol = 0;
        float bestScore = 0;
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

                if(mode == AlignmentMode.LOCAL && score <= 0){
                    move = Move.NIL;
                    score = 0;
                }

                if(mode == AlignmentMode.LOCAL && score > bestScore){
                    bestScore = score;
                    bestRow = row;
                    bestCol = col;
                }

                cost.set(row,col,score);
                traceback.set(row,col,move.ordinal());

            }

        }
        Alignment align;
        float endScore;
        switch (mode) {
            case LOCAL:
                align = getTraceback(traceback, bestRow, bestCol);
                endScore = bestScore; break;
            case GLOBAL:
                align = getTraceback(traceback, sizeB, sizeA);
                endScore = cost.get(sizeB,sizeA); break;
            default:
            case SEMIGLOBAL:
                for(int col = 0 ; col < sizeA+1; col++){
                    if(cost.get(sizeB, col) > bestScore){
                        bestScore = cost.get(sizeB,col);
                        bestRow = sizeB;
                        bestCol = col;
                    }
                }
                for(int row = 0 ; row < sizeB+1; row++){
                    if(cost.get(row, sizeA) > bestScore){
                        bestScore = cost.get(row,sizeA);
                        bestRow = row;
                        bestCol = sizeA;
                    }
                }
                align = getTraceback(traceback, bestRow, bestCol);
                endScore = bestScore; break;

        }
        return new AlignResult(endScore,align);
    }
}
