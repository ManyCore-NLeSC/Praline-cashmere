package nl.esciencecenter.praline.aligners;

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
        for(int row = 1 ; row < sizeB ; row++){
            cost.set(row,0, gapCostA.getGapCost(row));
            traceback.set(row,0, Move.TOP.ordinal());
        }
        for(int col = 1 ; col < sizeA ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceback.set(0,col, Move.LEFT.ordinal());
        }
        for(int row = 1; row < sizeB + 1; row++){
            for(int col = 1 ; col < sizeA + 1; col++){
                float gapA = Float.MIN_VALUE;
                for(int gap = 1 ; gap <= col ; gap++){
                    float c = cost.get(row,col-gap) + gapCostB.getGapCost(gap);
                    if(c > gapA) gapA = c;
                }
                float gapB = Float.MIN_VALUE;
                for(int gap = 1 ; gap <= row ; gap++){
                    float c = cost.get(row - gap,col) + gapCostA.getGapCost(gap);
                    if(c > gapB) gapB = c;
                }
                float match = cost.get(row-1,col - 1)
                        + posCosts.cost(col - 1, row-1);

                float score = match;
                Move move = Move.TOP_LEFT;

                if(gapA > score){
                    score = gapA;
                    move = Move.LEFT;
                }

                if(gapB > score){
                    score = gapB;
                    move = Move.TOP;
                }

                cost.set(row,col,score);
                traceback.set(row,col,move.ordinal());

            }
        }

        List<AlignStep> align = getTraceback(traceback, sizeA, sizeB);
        return new AlignResult(cost.get(sizeA,sizeB),align);
    }

    private List<AlignStep> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<Move> trace = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
        Move got;

        while((got = Move.values()[traceback.get(rowi,coli)]) != Move.NIL){
            trace.push(got);
            switch (got){
                case TOP: rowi--; break;
                case TOP_LEFT: rowi--; coli--; break;
                case LEFT: coli--; break;
            }
        }
       return moveToAlignSteps(trace);

    }

    private List<AlignStep> moveToAlignSteps(Stack<Move> trace) {
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
