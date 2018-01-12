package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;

import java.util.List;

public class O2AffineAligner implements IAlign{


    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        AffineGapCost gapCostA = ((AffineGapCost)  gapCostAg);
        AffineGapCost gapCostB = ((AffineGapCost)  gapCostBg);
        Matrix2DF alignCost = new Matrix2DF(sizeB,sizeA);
        Matrix2DF gapACost  = new Matrix2DF(sizeB, sizeA);
        Matrix2DF gapBCost  = new Matrix2DF(sizeB, sizeA);
//
//        Matrix2DI tracebackAlign = new Matrix2DI(sizeB, sizeA);
//        Matrix2DI tracebackGapA = new Matrix2DI(sizeB, sizeA);
//        Matrix2DI tracebackGapB = new Matrix2DI(sizeB, sizeA );
        alignCost.set(0,0,posCosts.cost(0,0));
        gapACost.set(0,0,Float.NEGATIVE_INFINITY);
        gapBCost.set(0, 0 ,Float.NEGATIVE_INFINITY);
//
//        tracebackAlign.set(0,0, AlignStep.NIL.ordinal());
//        tracebackGapA.set(0,0,AlignStep.NIL.ordinal());
//        tracebackGapB.set(0,0,AlignStep.NIL.ordinal());
        for(int row = 1 ; row < sizeB ; row++){
            alignCost.set(row,0, gapCostA.getGapCost(row));
//            tracebackAlign.set(row,0,AlignStep.GAPA.ordinal());
            gapBCost.set(row,0, Float.NEGATIVE_INFINITY);
//            tracebackGapB.set(row,0, AlignStep.NIL.ordinal());
        }
        for(int col = 1 ; col < sizeA ; col++){
            alignCost.set(0,col, gapCostB.getGapCost(col));
//            tracebackAlign.set(0,col,AlignStep.GAPB.ordinal());
            gapACost.set(0,col, Float.NEGATIVE_INFINITY);
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
                gapACost.set(row, col, extendGapA > newGapA ? extendGapA : newGapA);

                float newGapB = alignCost.get(row, col - 1) + gapCostB.start;
                float extendGapB = gapACost.get(row, col - 1) + gapCostB.extend;
                gapBCost.set(row, col, extendGapB > newGapB ? extendGapB : newGapB);

            }
        }
        float resMatch = alignCost.get(sizeB-1,sizeA-1);
            float resGapA = gapACost.get(sizeB-1,sizeA-1);
            float resGapB = gapBCost.get(sizeB-1,sizeA-1);

//        List<AlignStep> align = ReferenceO3Aligner.getTraceback(traceback, sizeB, sizeA);
//        return new AlignResult(cost.get(sizeB,sizeA),align);
        return new AlignResult(Math.max(resMatch,Math.max(resGapA,resGapB)),null);
    }
}
