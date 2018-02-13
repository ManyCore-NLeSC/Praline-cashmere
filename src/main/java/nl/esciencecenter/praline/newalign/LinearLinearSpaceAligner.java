package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;
import nl.esciencecenter.praline.data.Move;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class LinearLinearSpaceAligner implements  IAlign {
    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        float gapCostA = ((LinearGapCost)  gapCostAg).cost;
        float gapCostB = ((LinearGapCost)  gapCostBg).cost;

        LinkedList<Coordinate> alignCoords = new LinkedList<>();
        alignCoords.add(new Coordinate(0,0));
        double score = constructAlign(0, sizeA ,0, sizeB, gapCostA,gapCostB,posCosts, alignCoords);
        alignCoords.add(new Coordinate(sizeB, sizeA));

        return new AlignResult(score, coordsToSteps(alignCoords.iterator()));
    }

    static List<AlignStep> coordsToSteps(Iterator<Coordinate> c){
        List<AlignStep> res = new LinkedList<>();

        Coordinate prev = c.next();
        while(c.hasNext()){

            Coordinate cur = c.next();
            if(cur.x == prev.x && cur.y == prev.y + 1){
                res.add(AlignStep.GAPB);
            } else if(cur.x == prev.x + 1){
                if(cur.y == prev.y + 1){
                    res.add(AlignStep.ALIGN);
                } else if(cur.y == prev.y) {
                    res.add(AlignStep.GAPA);
                } else {
                    throw new Error("assumption violated (" + prev.x + "," + prev.y + ") (" + cur.x + "," + cur.y + ")");
                }
            } else {
                throw new Error("assumption violated (" + prev.x + "," + prev.y + ") (" + cur.x + "," + cur.y + ")");
            }
            prev = cur;
        }
        return res;
    }

    static int BORDER_BASE = 16000;

    // does not add begin/end
    double constructAlign(int startA,  int endA, int startB, int endB, float gapCostA, float gapCostB,
                                IPositionCost posCosts, LinkedList<Coordinate> alignCoords) {
        if(endA - startA + 1 <= BORDER_BASE || endB - startB <= BORDER_BASE ){
            return baseCase(startA,endA, startB, endB, gapCostA, gapCostB, posCosts,alignCoords);
        } else {
            int middleB = middleDown(startB, endB);
            ViaRes res = viaRow(startA, endA, startB, middleB, endB, gapCostA, gapCostB, posCosts);
            constructAlign(startA, res.via, startB, middleB, gapCostA, gapCostB, posCosts, alignCoords);
            alignCoords.add(new Coordinate(middleB, res.via));
            constructAlign(res.via, endA, middleB, endB, gapCostA, gapCostB, posCosts, alignCoords);
            return res.score;
        }

    }

    static double baseCase(int startA,  int endA, int startB, int endB, float gapCostA, float gapCostB,
                  IPositionCost posCosts, LinkedList<Coordinate> alignCoords){
        int diffA = endA - startA + 1;
        int diffB = endB - startB + 1;
        Matrix2DF cost = new Matrix2DF(diffB,diffA);
        Matrix2DI traceback = new Matrix2DI(diffB,diffA);
        cost.set(0,0,0);
        traceback.set(0,0, Move.NIL.ordinal());
        for(int row = 1 ; row < diffB ; row++){
            cost.set(row,0,gapCostA * row);
            traceback.set(row,0, Move.TOP.ordinal());
        }
        for(int col = 1 ; col < diffA ; col++){
            cost.set(0,col, gapCostB * col);
            traceback.set(0,col, Move.LEFT.ordinal());
        }
        for(int row = 1; row < diffB; row++){
            for(int col = 1 ; col < diffA; col++){
                float gapA = cost.get(row - 1,col) + gapCostA;
                float gapB = cost.get(row,col-1) + gapCostB;

                float match = cost.get(row-1,col - 1)
                        + posCosts.cost(startA + col - 1, startB + row-1);

                float score = match;

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
        int rowi = diffB - 1;
        int coli = diffA - 1;
        Move got;
        Stack<Coordinate> s = new Stack<>();
        while(! (rowi == 0 && coli == 0) && (got = Move.values()[traceback.get(rowi,coli)]) != Move.NIL ) {

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
            s.push(new Coordinate(startB + rowi, startA + coli));

        }
        s.pop();
        while(!s.isEmpty()){
            Coordinate c = s.pop();
            alignCoords.add(c);
           // System.out.printf("base (%d , %d )\n", c.x,c.y);

        }
        return cost.get(diffB - 1, diffA - 1);
    }

    int middleDown(int a, int b){
        return (a + b ) / 2;
    }



    static class ViaRes {
        final double score;
        final int via;

        ViaRes(double score, int via) {
            this.score = score;
            this.via = via;
        }
    }

    ViaRes viaRow(int startA,  int endA, int startB, int trackRow, int endB, float gapCostA, float gapCostB, IPositionCost posCosts){
        int aDiff = endA - startA + 1;
        double[] prevRow = new double[aDiff];
        double[] curRow  = new double[aDiff];
        for(int col = 0 ; col < aDiff ; col++){
            prevRow[col] = gapCostB * col;
        }


        for(int row = startB + 1; row <= trackRow; row++){
            curRow[0] = prevRow[0] + gapCostA;
            for(int col = 1; col < aDiff; col++){
                double gapA = prevRow[col] + gapCostA;
                double gapB = curRow[col-1] + gapCostB;

                double match = prevRow[col - 1]
                        + posCosts.cost(col + startA - 1, row-1);
                double score = match;

                if(gapA > score){ score = gapA;  }
                if(gapB > score){ score = gapB; }

                curRow[col] =score;
            }
            double[] tmp = prevRow;
            prevRow = curRow;
            curRow = tmp;
        }
        int[] viaCol = new int[aDiff];
        int[] viaColPrev = new int[aDiff];
        for(int i = 0 ; i < aDiff ; i++){
            viaColPrev[i] = i;
        }

        for(int row = trackRow + 1; row <= endB; row++){
            curRow[0] = prevRow[0] + gapCostA;
            viaCol[0] = viaColPrev[0];
            for(int col = 1; col < aDiff; col++){
                double gapA = prevRow[col] + gapCostA;
                double gapB = curRow[col-1] + gapCostB;

                double match = prevRow[col - 1]
                        + posCosts.cost(col + startA - 1, row-1);
                double score = match;
                int via = viaColPrev[col - 1];
                if(gapA > score){ score = gapA; via = viaColPrev[col]; }
                if(gapB > score){ score = gapB; via = viaCol[col - 1]; }
                viaCol[col] = via;
                curRow[col] =score;
            }
            double[] tmp = prevRow;
            prevRow = curRow;
            curRow = tmp;
            int[] viaTmp = viaColPrev;
            viaColPrev = viaCol;
            viaCol = viaTmp;
        }

        return new ViaRes(prevRow[aDiff-1],startA +  viaColPrev[aDiff-1]);
    }



    static double getScoreLinear(int sizeA, int sizeB, float gapCostA, float gapCostB, IPositionCost posCosts){
        double[] prevRow = new double[sizeA+1];
        double[] curRow  = new double[sizeA+1];
        for(int col = 0 ; col < sizeA + 1 ; col++){
            prevRow[col] = gapCostB * col;
        }

        for(int row = 1 ; row <sizeB + 1; row++){
            curRow[0] = prevRow[0] + gapCostA;
            for(int col = 1; col < sizeA + 1; col++){
                double gapA = prevRow[col] + gapCostA;
                double gapB = curRow[col-1] + gapCostB;

                double match = prevRow[col - 1]
                        + posCosts.cost(col - 1, row-1);

                double score = match;

                if(gapA > score){ score = gapA; }
                if(gapB > score){ score = gapB; }

                curRow[col] =score;
            }
            double[] tmp = prevRow;
            prevRow = curRow;
            curRow = tmp;
        }
        return prevRow[sizeA];
    }


}
