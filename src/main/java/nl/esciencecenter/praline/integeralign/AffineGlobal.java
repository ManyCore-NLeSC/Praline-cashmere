package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DI;

import java.util.List;
import java.util.Stack;

public class AffineGlobal {
    /*
    static final int GAPA_MASK=1, GAPB_MASK =2, ALIGN_MASK =4;

    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts, final boolean local) {
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
        Matrix2DI cost = new Matrix2DI(sizeB+1,sizeA+1);
        Matrix2DI traceBack = new Matrix2DI(sizeB +1, sizeA + 1);
        cost.set(0,0,0);
        traceBack.set(0,0,0);
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0,gapCostA.getGapCost(row));
            traceBack.set(row,0,GAPA_MASK);
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceBack.set(0,col,GAPB_MASK);
        }

        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                int traceA = traceBack.get(row-1,col);

                boolean gapAStarted = (traceA & GAPA_MASK) == GAPA_MASK;
                int gapA = cost.get(row - 1,col) +
                        (gapAStarted ? gapCostA.extend : gapCostA.start);
                int traceB = traceBack.get(row,col-1);
                boolean gapBStarted =  (traceA & GAPB_MASK) == GAPB_MASK;
                int gapB =  cost.get(row ,col - 1) +
                        (gapBStarted ? gapCostB.extend : gapCostB.start);


                int match =  cost.get(row-1,col - 1) +
                        posCosts.cost(col - 1, row-1);
                int score;
                int trace;
                if(gapA >= match){
                    if(gapA == gapB){
                        trace = GAPA_MASK | GAPB_MASK;
                        score = gapA;
                    } else if (gapB > gapA){
                        trace = GAPB_MASK;
                        score = gapB;
                    } else {
                        trace = GAPA_MASK;
                        score = gapA;
                    }
                } else if (gapB >= match){
                    trace = GAPB_MASK;
                    score = gapB;
                } else {
                    trace = ALIGN_MASK;
                    score = match;
                }



                cost.set(row,col,score);
                traceBack.set(row,col,trace);

            }

        }
//
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%4d ", cost.get(i,j));
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println();
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%4d ", traceBack.get(i,j));
//            }
//            System.out.println();
//        }
//
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

//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%d ", gapBStarted.get(i,j));
//            }
//            System.out.println();
//        }
        List<AlignStep> align = getTraceback(traceBack, sizeB, sizeA);
        return new AlignResult(cost.get(sizeB,sizeA),align);
    }
    //
    static List<AlignStep> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<AlignStep> res = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
        AlignStep lastStep = AlignStep.NIL;
        int trace;
        while((trace = traceback.get(rowi,coli)) == 0){
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


            res.push(newStep);
            lastStep = newStep;

        }
        return reverse(res);

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
*/
}
