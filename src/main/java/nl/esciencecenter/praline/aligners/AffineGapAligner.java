package nl.esciencecenter.praline.aligners;


import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.gapcost.LinearGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

import java.util.List;
import java.util.Stack;

public class AffineGapAligner implements IAlign {

    /* we need bitmasks to encode which directions
       we possibly can take, we need this
       to see if the gap is already started in a direction

       we also need this
       when doing the traceback because
       we need to keep walking in the same direction
       until we can no longer to pay the start cost
       */


    static final int GAPA_MASK=0b001, GAPB_MASK =0b010, ALIGN_MASK =0b100;

    static float max3(float a,float b, float c){
        return Math.max(a,Math.max(b,c));
    }

    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts, AlignmentMode mode) {
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
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB +1, sizeA + 1);
        cost.set(0,0,0);
        traceback.set(0,0,0);
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0,gapCostA.getGapCost(row));
            traceback.set(row,0,GAPA_MASK);
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceback.set(0,col,GAPB_MASK);
        }
        int bestRow = 0;
        int bestCol = 0;
        float bestScore = 0;

        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){

                int traceA = traceback.get(row-1,col);
                boolean gapAStarted = (traceA & GAPA_MASK) == GAPA_MASK;
                float gapA = cost.get(row - 1,col) +
                        (gapAStarted ? gapCostA.extend : gapCostA.start);
		
                int traceB = traceback.get(row,col-1);
                boolean gapBStarted =  (traceB & GAPB_MASK) == GAPB_MASK;
                float gapB =  cost.get(row ,col - 1) +
                        (gapBStarted ? gapCostB.extend : gapCostB.start);


                float match =  cost.get(row-1,col - 1) +
                        posCosts.cost(col - 1, row-1);
		
                float score = max3(gapA,gapB,match);
                int trace = 0 ;
                if(mode == AlignmentMode.LOCAL && score <= 0){
                    score = 0;
                }
                if(gapA == score) {
                    if(gapB == score) {
                        trace = GAPA_MASK | GAPB_MASK;
                    } else {
                        trace = GAPA_MASK;
                    }
                } else if (gapB == score) {
                    trace |= GAPB_MASK;
                } else if(match == score){
                    trace = ALIGN_MASK;
                }

                if(mode == AlignmentMode.LOCAL && score <= 0){
                    score = 0;
                    trace = 0;
                }

                if(mode == AlignmentMode.LOCAL && score > bestScore){
                    bestScore = score;
                    bestRow = row;
                    bestCol = col;
                }


                cost.set(row,col,score);
                traceback.set(row,col,trace);

            }

        }


        List<Coordinate> align;
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
//
    static List<Coordinate> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        int rowi = rowstart;
        int coli = colstart;
        Stack<Coordinate> res = new Stack<>();
        res.add(new Coordinate(rowi,coli));

        AlignStep lastStep = AlignStep.NIL;
        /* We need to keep walking in the same
           direction until the start cost is paid

           Hence we remember the direction we took last
           and keep walking that way until we can no longer
           walk in that direction.
         */

        int trace;
        while((trace = traceback.get(rowi,coli)) != 0){
            AlignStep newStep;
            if(trace == ALIGN_MASK){
                newStep = AlignStep.ALIGN;
                rowi--; coli--;
            } else if (trace == GAPA_MASK){
                newStep = AlignStep.GAPA;
                rowi--;
            } else if (trace == GAPB_MASK){
                newStep = AlignStep.GAPB;
                coli--;
            } else { // must be both gap a and b

                if(lastStep == AlignStep.GAPA){
                    newStep = lastStep;
                    rowi--;
                } else {
                    newStep = AlignStep.GAPB;
                    coli--;
                }
            }
            res.add(new Coordinate(rowi,coli));
            lastStep = newStep;

        }
        Stack<Coordinate> steps =  ReferenceAligner.reverse(res);
        return steps;

    }


}
