package nl.esciencecenter.praline.integeralign;


import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.newalign.Coordinate;

import java.util.List;
import java.util.Stack;

public class AffineAlign implements IAlign {

    static final int GAPA_MASK=1, GAPB_MASK =2, ALIGN_MASK =4;

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
                    bestRow = row;
                    bestCol = col;
                }


                cost.set(row,col,score);
                traceback.set(row,col,trace);

            }

        }

//        System.out.println();
//
//        System.out.println("traceback\n");
//
//        for(int i = 0 ; i < sizeB + 1; i++){
//            for(int j = 0 ; j < sizeA + 1 ; j++){
//                System.out.printf("%4d ", traceback.get(i,j));
//            }
//            System.out.println();
//        }
//        System.out.println();


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
//
    static Alignment getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<AlignStep> res = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
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

//            System.out.println(newStep);
            res.push(newStep);
            lastStep = newStep;

        }
        Stack<AlignStep> steps =  reverse(res);
        return new Alignment(new Coordinate(rowi,coli),steps);

    }

    static Stack<AlignStep> reverse(Stack<AlignStep> e){
        Stack<AlignStep> res = new Stack<>();
        while(!e.isEmpty()){
            res.push(e.pop());
        }
        return res;
    }
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
