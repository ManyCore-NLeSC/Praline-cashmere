package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.LinearGapCost;

import java.util.List;
import java.util.Stack;


public class InlineAffineGapProfileAligner {

    static final int GAPA_MASK=1, GAPB_MASK =2, ALIGN_MASK =4;

    static float max3(float a,float b, float c){
        return Math.max(a,Math.max(b,c));
    }

    static float positionCost(int posA, int posB,Matrix2DF[] profileA, Matrix2DF[] profileB, Matrix2DF[] costs ){
        float posCost = 0;
        for(int track = 0; track < costs.length; track++){ ;
            for(int i = 0 ; i < profileA[track].nrCols() ; i++){
                for(int j = 0 ; j < profileA[track].nrCols() ; j++){
                    posCost += costs[track].get(i,j) * profileA[track].get(posA,i) * profileB[track].get(posB,j);
                }
            }
        }
        return posCost;
    }


    static AlignResult align(Matrix2DF[] profileA, Matrix2DF[] profileB, Matrix2DF[] costs, float gapCostStart, float gapCostExtend, AlignmentMode mode){

        // initialization
        int sizeA = profileA[0].nrRows;
        int sizeB = profileB[0].nrRows;
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB +1, sizeA + 1);
        cost.set(0,0,0);
        traceback.set(0,0,0);
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0,gapCostStart + (row - 1) * gapCostExtend);
            traceback.set(row,0,GAPA_MASK);
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostStart + (col - 1) * gapCostExtend);
            traceback.set(0,col,GAPB_MASK);
        }
        int bestRow = 0;
        int bestCol = 0;
        float bestScore = 0;

        // core compute loop
        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                int traceA = traceback.get(row-1,col);

                boolean gapAStarted = (traceA & GAPA_MASK) == GAPA_MASK;
                float gapA = cost.get(row - 1,col) +
                        (gapAStarted ? gapCostExtend : gapCostStart);
                int traceB = traceback.get(row,col-1);
                boolean gapBStarted =  (traceB & GAPB_MASK) == GAPB_MASK;
                float gapB =  cost.get(row ,col - 1) +
                        (gapBStarted ? gapCostExtend : gapCostStart);

                float match =  cost.get(row-1,col - 1) +
                        positionCost(row-1, col-1, profileA,profileB, costs);


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

        // get traceback
        List<Coordinate> align;
        float endScore;
        switch (mode) {
            case LOCAL:
                align = AffineGapAligner.getTraceback(traceback, bestRow, bestCol);
                endScore = bestScore; break;
            case GLOBAL:
                align = AffineGapAligner.getTraceback(traceback, sizeB, sizeA);
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
                align = AffineGapAligner.getTraceback(traceback, bestRow, bestCol);
                endScore = bestScore; break;

        }
        return new AlignResult(endScore,align);
    }


    static List<Coordinate> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        int rowi = rowstart;
        int coli = colstart;
        Stack<Coordinate> res = new Stack<>();
        res.add(new Coordinate(rowi,coli));

        AlignStep lastStep = AlignStep.NIL;

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
