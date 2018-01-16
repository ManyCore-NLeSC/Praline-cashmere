package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.data.PositionCost;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ReferenceO3Aligner implements IAlign{


    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost posCosts) {
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        cost.set(0,0,0);
        traceback.set(0,0,Move.NIL.ordinal());
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0, gapCostA.getGapCost(row));
            traceback.set(row,0, Move.TOP.ordinal());
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceback.set(0,col, Move.LEFT.ordinal());
        }
        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                double gapA = Double.NEGATIVE_INFINITY;
                for(int gap = 1 ; gap <= row ; gap++){
                    double c = cost.get(row - gap,col) + gapCostA.getGapCost(gap);
                    if(c > gapA) gapA = c;
                }
                double gapB = Double.NEGATIVE_INFINITY;
                for(int gap = 1 ; gap <= col ; gap++){
                    double c = cost.get(row,col-gap) + gapCostB.getGapCost(gap);
                    if(c > gapB) gapB = c;
                }

                double match = cost.get(row-1,col - 1)
                        + posCosts.cost(col - 1, row-1);

                double score = match;

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
//        for(int i = 0 ; i < sizeB + 1 ; i++){
//            for(int j = 0; j < sizeA + 1 ; j++){
//                System.out.printf("%f ", cost.get(i,j));
//            }
//            System.out.println();
//        }
        List<AlignStep> align = getTraceback(traceback, sizeB , sizeA);
        return new AlignResult(cost.get(sizeB,sizeA),align);
    }

    static List<AlignStep> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<Move> trace = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
        Move got;

        while((got = Move.values()[traceback.get(rowi,coli)]) != Move.NIL){

            trace.push(got);
            switch (got){
                case TOP: rowi--; break;
                case TOP_LEFT:   rowi--; coli--; break;
                case LEFT: coli--; break;
            }
        }
       return moveToAlignSteps(trace);

    }

    static List<AlignStep> moveToAlignSteps(Stack<Move> trace) {
        LinkedList<AlignStep> res = new LinkedList<>();
        while(!trace.isEmpty()){

            switch(trace.pop()){
                case TOP_LEFT: res.add(AlignStep.ALIGN); break;
                case TOP: res.add(AlignStep.GAPA); break;
                case LEFT: res.add(AlignStep.GAPB); break;
            }
        }
        return res;
    }
}
