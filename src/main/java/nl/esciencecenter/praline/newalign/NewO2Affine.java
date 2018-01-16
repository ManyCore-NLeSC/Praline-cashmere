package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NewO2Affine implements IAlign {

    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        AffineGapCost gapCostA = ((AffineGapCost)  gapCostAg);
        AffineGapCost gapCostB = ((AffineGapCost)  gapCostBg);
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI gapAStarted = new Matrix2DI(sizeB+1, sizeA+1);
        Matrix2DI gapBStarted = new Matrix2DI(sizeB+1, sizeA+1);
        //Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        cost.set(0,0,0);
        gapAStarted.set(0,0,0);
        //traceback.set(0,0, AffineMove.NIL.ordinal());

        //traceback.set(1,0,AffineMove.GAPA_START.ordinal());
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0,gapCostA.getGapCost(row));
            gapAStarted.set(row,0,1);
            gapBStarted.set(row,0,0);
            //traceback.set(row,0, AffineMove.GAPA_START.ordinal());
        }
        //traceback.set(0,1,AffineMove.GAPB_START.ordinal());
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            gapAStarted.set(0,col,0);
            gapBStarted.set(0,col,1);
            //traceback.set(0,col, AffineMove.GAPB_START.ordinal());
        }
        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                //AffineMove gapAPrev = AffineMove.values()[traceback.get(row-1,col)];
                double gapA = cost.get(row - 1,col) +
                        (gapAStarted.get(row-1,col) == 1 ? gapCostA.extend : gapCostA.start);


                double gapB =  cost.get(row ,col - 1) +
                        (gapBStarted.get(row,col - 1) == 1 ? gapCostB.extend : gapCostB.start);


                double match = cost.get(row-1,col - 1)
                        + posCosts.cost(col - 1, row-1);

                double score = Math.max(match, Math.max(gapA,gapB));
                gapAStarted.set(row,col,gapA >= score ? 1 : 0);

                gapBStarted.set(row,col,gapB >= score ? 1 : 0);

                cost.set(row,col,score);
                //traceback.set(row,col,move.ordinal());

            }

        }
//
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%f ", cost.get(i,j));
//            }
//            System.out.println();
//        }
//
//            System.out.println();
//
//
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%d ", gapAStarted.get(i,j));
//            }
//            System.out.println();
//        }
//
//        System.out.println();
//
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%d ", gapBStarted.get(i,j));
//            }
//            System.out.println();
//        }
//        List<AlignStep> align = getTraceback(traceback, sizeB, sizeA);
        return new AlignResult((float)cost.get(sizeB,sizeA),null);
    }
//
//    static List<AlignStep> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
//        Stack<AffineMove> trace = new Stack<>();
//        int rowi = rowstart;
//        int coli = colstart;
//        AffineMove got;
//
//        while((got = AffineMove.values()[traceback.get(rowi,coli)]) != AffineMove.NIL){
//            //System.out.printf("%d %d %d %d\n", rowi, coli, got.ordinal(), AffineMove.NIL.ordinal());
//            trace.push(got);
//            switch (got){
//                case GAPA_START: rowi--; break;
//                case ALIGN:   rowi--; coli--; break;
//                case GAPB_START: coli--; break;
//            }
//
//        }
//        return moveToAlignSteps(trace);
//
//    }
//
//    private static List<AlignStep> moveToAlignSteps(Stack<AffineMove> trace) {
//        List<AlignStep> res = new ArrayList<>();
//        while(!trace.isEmpty()){
//            AlignStep step;
//            switch (trace.pop()){
//
//                case GAPA_START: step = AlignStep.GAPA; break;
//                case GAPB_START: step = AlignStep.GAPB; break;
//                default: step = AlignStep.ALIGN; break;
//            }
//            res.add(step);
//        }
//        return res;
//    }
}
