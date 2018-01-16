package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;

import java.util.List;

public class O2AffineAligner {


    static float max3(float a, float b , float c){
        return Math.max(a,Math.max(b,c));
    }
/*
    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        AffineGapCost gapCostA = ((AffineGapCost)  gapCostAg);
        AffineGapCost gapCostB = ((AffineGapCost)  gapCostBg);
        Matrix2DF alignCost = new Matrix2DF(sizeB, sizeA);
        Matrix2DF gapACost  = new Matrix2DF(sizeB, sizeA);
        Matrix2DF gapBCost  = new Matrix2DF(sizeB, sizeA);
//
//        Matrix2DI tracebackAlign = new Matrix2DI(sizeB, sizeA);
//        Matrix2DI tracebackGapA = new Matrix2DI(sizeB, sizeA);
//        Matrix2DI tracebackGapB = new Matrix2DI(sizeB, sizeA );
        alignCost.set(0,0,posCosts.cost(0,0));
        gapACost.set(0,0,gapCostA.start);
        gapBCost.set(0, 0 ,gapCostB.start);
//
//        tracebackAlign.set(0,0, AlignStep.NIL.ordinal());
//        tracebackGapA.set(0,0,AlignStep.NIL.ordinal());
//        tracebackGapB.set(0,0,AlignStep.NIL.ordinal());
        for(int row = 1 ; row < sizeB ; row++){
            alignCost.set(row,0, posCosts.cost(0,row) + gapCostA.getGapCost(row));
//            tracebackAlign.set(row,0,AlignStep.GAPA.ordinal());
            gapBCost.set(row,0, gapBCost.get(row-1, 0) + gapCostB.extend);
            float newGapA = alignCost.get(row - 1, 0) + gapCostA.start;
            float extendGapA = gapACost.get(row - 1, 0) + gapCostA.extend;
            float newGapAB = gapBCost.get(row - 1, 0) + gapCostA.start;
            gapACost.set(row, 0, max3(newGapA, newGapAB, extendGapA));
//            tracebackGapB.set(row,0, AlignStep.NIL.ordinal());
        }
        for(int col = 1 ; col < sizeA ; col++){
            alignCost.set(0,col, posCosts.cost(col,0) + gapCostB.getGapCost(col));
//            tracebackAlign.set(0,col,AlignStep.GAPB.ordinal());
            gapACost.set(0,col, gapACost.get(0,col-1) + gapCostA.extend);
            float newGapB = alignCost.get(0, col - 1) + gapCostB.start;
            float newGapBA = gapACost.get(0, col - 1) + gapCostB.start; // gap A after gap B
            float extendGapB = gapBCost.get(0, col - 1) + gapCostB.extend;

            gapBCost.set(0, col, max3(newGapB, newGapBA, extendGapB));
//            tracebackGapB.set(0,col, AlignStep.NIL.ordinal());
        }
        for(int row = 1; row < sizeB ; row++) {
            for (int col = 1; col < sizeA; col++) {

                float matchScore = posCosts.cost(col, row);
                float matchPrev = alignCost.get(row - 1, col - 1);
                float gapAPrev = gapACost.get(row - 1, col - 1);
                float gapBPrev = gapBCost.get(row - 1, col - 1);
                float newMatch = matchPrev;
                if (gapAPrev > newMatch) {
                    newMatch = gapAPrev;
                }
                if (gapBPrev > newMatch) {
                    newMatch = gapBPrev;
                }
                alignCost.set(row, col, newMatch + matchScore);
                float newGapA = alignCost.get(row - 1, col) + gapCostA.start;
                float extendGapA = gapACost.get(row - 1, col) + gapCostA.extend;
                float newGapAB = gapBCost.get(row - 1, col) + gapCostA.start;
                gapACost.set(row, col, max3(newGapA, newGapAB, extendGapA));

                float newGapB = alignCost.get(row, col - 1) + gapCostB.start;
                float extendGapB = gapACost.get(row, col - 1) + gapCostB.extend;
                float newGapBA = gapACost.get(row, col - 1) + gapCostB.start; // gap A after gap B
                gapBCost.set(row, col, max3(newGapB, newGapBA, extendGapB));

            }
        }
        System.out.println("Align");

        for(int i = 0 ; i < sizeB ; i++){
            for(int j = 0 ; j < sizeA; j++){
                System.out.printf("%f ", alignCost.get(i,j));
            }
            System.out.println();
        }
        System.out.println("GapA");

        for(int i = 0 ; i < sizeB ; i++){
            for(int j = 0 ; j < sizeA; j++){
                System.out.printf("%f ", gapACost.get(i,j));
            }
            System.out.println();
        }
        System.out.println("GapB");

        for(int i = 0 ; i < sizeB ; i++){
            for(int j = 0 ; j < sizeA; j++){
                System.out.printf("%f ", gapBCost.get(i,j));
            }
            System.out.println();
        }

        float resMatch = alignCost.get(sizeB-1,sizeA-1);
            float resGapA = gapACost.get(sizeB-1,sizeA-1);
            float resGapB = gapBCost.get(sizeB-1,sizeA-1);
            float score = Math.max(resMatch,Math.max(resGapA, resGapB));

//        List<AlignStep> align = ReferenceO3Aligner.getTraceback(traceback, sizeB, sizeA);
//        return new AlignResult(cost.get(sizeB,sizeA),align);
        return new AlignResult(Math.max(resMatch,Math.max(resGapA,resGapB)),null);
    }
    */
}
