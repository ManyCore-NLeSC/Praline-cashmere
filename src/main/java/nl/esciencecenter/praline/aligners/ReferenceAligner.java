package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

import java.util.List;
import java.util.Stack;

public class ReferenceAligner implements IAlign {


    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost posCosts, AlignmentMode mode) {
        Matrix2DF cost = new Matrix2DF(sizeB+1,sizeA+1);
        Matrix2DI traceback = new Matrix2DI(sizeB+1, sizeA+1);
        int bestRow = 0;
        int bestCol = 0;
        float bestScore = 0;
        cost.set(0,0,0);
        traceback.set(0,0, AlignStep.NIL.ordinal());
        for(int row = 1 ; row < sizeB + 1 ; row++){
            cost.set(row,0, gapCostA.getGapCost(row));
            traceback.set(row,0, AlignStep.GAPA.ordinal());
        }
        for(int col = 1 ; col < sizeA + 1 ; col++){
            cost.set(0,col, gapCostB.getGapCost(col));
            traceback.set(0,col, AlignStep.GAPB.ordinal());
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

                AlignStep move = AlignStep.ALIGN;

                if(gapA >= score){
                    score = gapA;
                    move = AlignStep.GAPA;
                }

                if(gapB >= score){
                    score = gapB;
                    move = AlignStep.GAPB;
                }
                if(mode == AlignmentMode.LOCAL && score <= 0){
                    move = AlignStep.NIL;
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

    public static List<Coordinate> getTraceback(Matrix2DI traceback, int rowstart, int colstart) {
        Stack<Coordinate> trace = new Stack<>();
        int rowi = rowstart;
        int coli = colstart;
        trace.push(new Coordinate(rowi,coli));
        AlignStep got;

        while((got = AlignStep.values()[traceback.get(rowi,coli)]) != AlignStep.NIL) {
            switch (got) {
                case GAPA:
                    rowi--;
                    break;
                case ALIGN:
                    rowi--;
                    coli--;
                    break;
                case GAPB:
                    coli--;
                    break;
            }
            trace.push(new Coordinate(rowi,coli));
        }
        return reverse(trace);

    }


    static Stack<Coordinate> reverse(Stack<Coordinate> e){
        Stack<Coordinate> res = new Stack<>();
        while(!e.isEmpty()){
            Coordinate c = e.pop();
            res.push(new Coordinate(c.getY(),c.getX()));
        }
        return res;
    }

}
