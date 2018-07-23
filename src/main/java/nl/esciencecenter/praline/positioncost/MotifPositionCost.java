package nl.esciencecenter.praline.positioncost;

import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.Matrix2DI;

public class MotifPositionCost implements IPositionCost {
    final Matrix2DI a,b;
    final Matrix2DF[] alignCosts;

    public MotifPositionCost(Matrix2DI a, Matrix2DI b, Matrix2DF[] alignCosts){
        if(a.nrRows != b.nrRows) {
            throw new Error("Not the same number of tracks" + a.nrRows + " " + b.nrRows + "cols " + a.nrCols + " " + b.nrCols);
        }
        this.a = a;
        this.b = b;
        this.alignCosts = alignCosts;

    }

    @Override
    public float cost(int posA, int posB) {
        int cost = 0;
        for(int track = 0; track < alignCosts.length; track++){
            int ac;
            int bc;
            if(posA >= a.nrCols || posB >= b.nrCols){
                System.err.printf("OUTOFBOUNDS\n");
                System.err.printf("track %d rows %d cols %d\n", track, a.nrRows, b.nrRows);
                System.err.printf("ding %d %d\n",  posA,posB);
                throw new Error("cost out of bounds!");
            } else {

                ac = a.get(track,posA);
                bc = b.get(track,posB);
            }
            if(ac >= alignCosts[track].nrRows || bc >= alignCosts[track].nrCols){
                System.err.printf("OUTOFBOUNDS\n");
                System.err.printf("track %d rows %d cols %d\n", track, alignCosts[track].nrRows, alignCosts[track].nrCols);
                System.err.printf("ding %d %d\n",  ac,bc);
                throw new Error("cost out of bounds!");
            } else {
                cost+=alignCosts[track].get(ac, bc);
            }
            //System.out.printf("track %d rows %d cols %d\n", track, alignCosts[track].nrRows, alignCosts[track].nrCols);
            //System.out.printf("ding %d %d\n",  ac, bc);


        }
        return cost;
    }
}
