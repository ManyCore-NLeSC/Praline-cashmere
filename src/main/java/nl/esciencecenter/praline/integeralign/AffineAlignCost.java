package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.newalign.Coordinate;

import java.util.Stack;

public class AffineAlignCost implements IAlign{

    static final int GAPA_MASK=1, GAPB_MASK =2, ALIGN_MASK =4;

    static float max3(float a,float b, float c){
        return Math.max(a,Math.max(b,c));
    }

    public float alignCost(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts, AlignmentMode mode) {
        AffineGapCost gapCostA;
        if(gapCostAg instanceof LinearGapCost) {
            int cost = ((LinearGapCost)gapCostAg).cost;
            gapCostA = new AffineGapCost(cost,cost);
        } else {
            gapCostA = ((AffineGapCost)  gapCostAg);
        }
        AffineGapCost gapCostB;
        if(gapCostBg instanceof LinearGapCost) {
            int cost = ((LinearGapCost)gapCostBg).cost;
            gapCostB = new AffineGapCost(cost,cost);
        } else {
            gapCostB = ((AffineGapCost)  gapCostBg);
        }
        Matrix2DF cost = new Matrix2DF(2,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(2, sizeA + 1);
        cost.set(0,0,0);
        traceback.set(0,0,0);
        for(int row = 1 ; row < 2; row++){
            cost.set(row,0,gapCostA.getGapCost(row));
            traceback.set(row,0,GAPA_MASK);
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceback.set(0,col,GAPB_MASK);
        }
        float bestScore = 0;
        int prevRow = 0;
        int curRow = 1;
        for(int row = 1; row < sizeB + 1; row++){
            cost.set(curRow, 0, gapCostA.getGapCost(row));
            traceback.set(curRow,0,GAPA_MASK);

            for(int col = 1 ; col < sizeA + 1; col++){
                int traceA = traceback.get(prevRow,col);

                boolean gapAStarted = (traceA & GAPA_MASK) == GAPA_MASK;
                float gapA = cost.get(prevRow,col) +
                        (gapAStarted ? gapCostA.extend : gapCostA.start);
                int traceB = traceback.get(curRow,col-1);
                boolean gapBStarted =  (traceB & GAPB_MASK) == GAPB_MASK;
                float gapB =  cost.get(curRow ,col - 1) +
                        (gapBStarted ? gapCostB.extend : gapCostB.start);


                float match =  cost.get(prevRow,col - 1) +
                        posCosts.cost(col - 1, row-1);
                float score = max3(gapA,gapB,match);
                int trace;
                if(mode == AlignmentMode.LOCAL && score <= 0){
                    trace = 0;
                    score = 0;
                } else if(gapA == score){
                    if(gapB == score){
                        trace = GAPB_MASK | GAPA_MASK;
                    } else {
                        trace = GAPA_MASK;
                    }
                } else if(gapB == score){
                    trace = GAPB_MASK;
                } else {
                    trace = ALIGN_MASK;
                }

                if(mode == AlignmentMode.LOCAL && score > bestScore){
                    bestScore = score;
                } else if (mode == AlignmentMode.SEMIGLOBAL && col == sizeA && score > bestScore){
                    bestScore = score;
                }


                cost.set(curRow,col,score);
                traceback.set(curRow,col,trace);

            }

            int tmp = curRow;
            curRow = prevRow;
            prevRow = tmp;

        }



        float endScore;
        switch (mode) {
            case LOCAL:
                endScore = bestScore; break;
            case GLOBAL:
                endScore = cost.get(prevRow,sizeA); break;
            default:
            case SEMIGLOBAL:
                for(int col = 0 ; col < sizeA+1; col++){
                    if(cost.get(prevRow, col) > bestScore){
                        bestScore = cost.get(prevRow,col);
                    }
                }
                endScore = bestScore; break;

        }
        return endScore;
    }

    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost costs, AlignmentMode mode) {
        float score = alignCost(sizeA, sizeB, gapCostA, gapCostB, costs, mode);
        return new AlignResult(score, null);
    }
}
