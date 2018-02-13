package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.newalign.Coordinate;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ReferenceO3Aligner implements IAlign {


    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost posCosts,AlignmentMode mode) {
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        int bestRow = 0;
        int bestCol = 0;
        float bestScore = 0;
        cost.set(0,0,0);
        traceback.set(0,0, Move.NIL.ordinal());
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
                float gapA = Integer.MIN_VALUE;
                for(int gap = 1 ; gap <= row ; gap++){
                    float c = cost.get(row - gap,col) + gapCostA.getGapCost(gap);
                    if(c > gapA) gapA = c;
                }
                float gapB = Integer.MIN_VALUE;
                for(int gap = 1 ; gap <= col ; gap++){
                    float c = cost.get(row,col-gap) + gapCostB.getGapCost(gap);
                    if(c > gapB) gapB = c;
                }

                float match = cost.get(row-1,col - 1)
                        + posCosts.cost(col - 1, row-1);

                float score = match;

                Move move = Move.TOP_LEFT;

                if(gapA >= score){
                    score = gapA;
                    move = Move.TOP;
                }

                if(gapB >= score){
                    score = gapB;
                    move = Move.LEFT;
                }
                if(mode == AlignmentMode.LOCAL && score <= 0){
                    move = Move.NIL;
                    score = 0;
                }

                if(mode == AlignmentMode.LOCAL && score > bestScore){
                    bestScore = score;
                    bestRow = row;
                    bestCol = col;
                }
                cost.set(row,col,score);

                traceback.set(row,col,move.ordinal());

            }


        }

//        for(int i = 0 ; i < sizeB + 1; i++){
//            for(int j = 0 ; j < sizeA + 1 ; j++){
//                System.out.printf("%4d ", cost.get(i,j));
//            }
//            System.out.println();
//        }
//        System.out.println();
//
//
//        for(int i = 0 ; i < sizeB + 1; i++){
//            for(int j = 0 ; j < sizeA + 1 ; j++){
//                System.out.printf("%8s ", Move.values()[traceback.get(i,j)]);
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

    static Alignment getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<Move> trace = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
        Move got;

        while((got = Move.values()[traceback.get(rowi,coli)]) != Move.NIL) {
            trace.push(got);
            switch (got) {
                case TOP:
                    rowi--;
                    break;
                case TOP_LEFT:
                    rowi--;
                    coli--;
                    break;
                case LEFT:
                    coli--;
                    break;
            }
        }
        List<AlignStep> steps =  moveToAlignSteps(trace);
        return new Alignment(new Coordinate(rowi,coli), steps);

    }

    static List<AlignStep> moveToAlignSteps(Stack<Move> trace) {
        LinkedList<AlignStep> res = new LinkedList<>();
        while(!trace.isEmpty()){
            AlignStep ns;
            switch(trace.pop()){
                case TOP_LEFT: ns = AlignStep.ALIGN; break;
                case TOP: ns = AlignStep.GAPA; break;
                case LEFT: default: ns = AlignStep.GAPB; break;
            }
//            System.out.println(ns);
            res.add(ns);
        }

        return res;
    }
}
