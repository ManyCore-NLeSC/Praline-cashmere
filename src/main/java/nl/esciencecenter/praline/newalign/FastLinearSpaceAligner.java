package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.data.Matrix2DI;

import java.util.LinkedList;

import static nl.esciencecenter.praline.newalign.LinearLinearSpaceAligner.baseCase;
import static nl.esciencecenter.praline.newalign.LinearLinearSpaceAligner.coordsToSteps;

public class FastLinearSpaceAligner  {
    /*

    final int borderBase ;
    final int nrTrackRows;

    public FastLinearSpaceAligner(int borderBase, int nrTrackRows) {
        this.borderBase = borderBase;
        this.nrTrackRows = nrTrackRows;
    }


    @Override
    public AlignResult align(int sizeA, int sizeB, IGapCost gapCostAg, IGapCost gapCostBg, IPositionCost posCosts) {
        double gapCostA = ((LinearGapCost)  gapCostAg).cost;
        double gapCostB = ((LinearGapCost)  gapCostBg).cost;
        LinkedList<Coordinate> alignCoords = new LinkedList<>();
        alignCoords.add(new Coordinate(0,0));
        double score = constructAlign(0,sizeA,0,sizeB, gapCostA, gapCostB, posCosts, alignCoords);
        alignCoords.add(new Coordinate(sizeB, sizeA));

        return new AlignResult(score, coordsToSteps(alignCoords.iterator()));

    }

    int[] divideEven(int start, int end, int nr){
        int diff = end + 1 - start;
        float inc = diff / nr;
        int[] res = new int[nr -1 ];
        for(int i = 0 ; i < nr - 1  ; i++){
            res[i] = (int) ((i + 1) * inc + start);
        }
        return res;
    }



    // does not add begin/end
    double constructAlign(int startA,  int endA, int startB, int endB, double gapCostA, double gapCostB,
                         IPositionCost posCosts, LinkedList<Coordinate> alignCoords) {
        //System.out.printf("constructAlgign(%d, %d , %d, %d)\n", startA, endA, startB, endB);
        if(endA - startA + 1 <= borderBase || endB - startB <= borderBase ){
            return baseCase(startA,endA, startB, endB, gapCostA, gapCostB, posCosts,alignCoords);
        } else {
            int trackRows = Math.max((startB - endB - 1) / borderBase, 2);
            trackRows = trackRows > nrTrackRows ? nrTrackRows : trackRows;
            int[] track = divideEven(startB,endB, trackRows);
//            for(int i = 0 ; i < track.length ; i++){
//                System.out.printf("track %d\n",track[i]);
//            }
            ViaRes res = viaRow(startA,endA, startB, endB, track, gapCostA, gapCostB, posCosts);
            int sa = startA;
            int sb = startB ;
            for(int i = 0 ; i < res.vias.length; i++){
                constructAlign(sa, res.vias[i], sb, track[i], gapCostA, gapCostB, posCosts, alignCoords);
                alignCoords.add(new Coordinate(track[i],res.vias[i]));
                //System.out.printf("rec (%d , %d )\n", track[i], res.vias[i]);
                sa = res.vias[i];
                sb = track[i];
            }
            constructAlign(sa, endA, sb, endB, gapCostA, gapCostB, posCosts, alignCoords);
            return res.score;
        }

    }


    static class ViaRes {
        final double score;
        final int[] vias;

        ViaRes(double score, int[] vias) {
            this.score = score;
            this.vias = vias;
        }
    }

    ViaRes viaRow(int startA, int endA, int startB, int endB, int[] vias,  double gapCostA, double gapCostB, IPositionCost posCosts){
        //System.out.printf("viaRow(%d, %d, %d, %d) %d\n", startA, endA, startB, endB, vias.length);
        int aDiff = endA - startA + 1;
        int bDiff = endB - startB + 1;
        double[] prevRow = new double[aDiff];
        double[] curRow  = new double[aDiff];
        for(int col = 0 ; col < aDiff ; col++){
            prevRow[col] = gapCostB * col;
        }
        Matrix2DI viaCol = new Matrix2DI(vias.length, aDiff);
        Matrix2DI viaColPrev = new Matrix2DI(vias.length, aDiff);
        int curTrackRow = 0;
        for(int row = startB + 1; row <= endB; row++){
            if(curTrackRow < vias.length && row == vias[curTrackRow] + 1){
                for(int col = 0 ; col < aDiff; col++){
                    viaColPrev.set(curTrackRow, col, col);
                }
                curTrackRow++;
            }
            curRow[0] = prevRow[0] + gapCostA;
            for(int t = 0 ; t < curTrackRow; t++){
                viaCol.set(t,0,viaColPrev.get(t,0));
            }

            for(int col = 1; col < aDiff; col++){
                double gapA = prevRow[col] + gapCostA;
                double gapB = curRow[col-1] + gapCostB;

                double match = prevRow[col - 1]
                        + posCosts.cost(col + startA - 1, row-1);
                double score = match;
                AlignStep choice = AlignStep.ALIGN;
                if(gapA > score){ score = gapA;  choice = AlignStep.GAPA; }
                if(gapB > score){ score = gapB; choice = AlignStep.GAPB;  }

                curRow[col] =score;
                switch (choice) {
                    case ALIGN:
                        for(int t = 0 ; t < curTrackRow; t++){
                            viaCol.set(t,col,viaColPrev.get(t,col-1));
                        }
                        break;
                    case GAPA:
                        for(int t = 0 ; t < curTrackRow; t++){
                            viaCol.set(t,col,viaColPrev.get(t,col));
                        }
                        break;
                    case GAPB:
                        for(int t = 0 ; t < curTrackRow; t++){
                            viaCol.set(t,col,viaCol.get(t,col - 1));
                        }
                        break;
                }
            }
            double[] tmp = prevRow;
            prevRow = curRow;
            curRow = tmp;
            Matrix2DI tmpM = viaColPrev;
            viaColPrev = viaCol;
            viaCol = tmpM;
        }

        int[] viaRes = new int[vias.length];

        for(int t = 0 ; t < vias.length ; t++){
            viaRes[t] = startA + viaColPrev.get(t,aDiff-1);
           // System.out.printf("Setting %d %d\n",t, viaColPrev.get(t,aDiff-1));
        }


        return new ViaRes(prevRow[aDiff-1],viaRes);
    }
    */
}
