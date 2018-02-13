package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix2DI;

public class MotifPositionCost implements IPositionCost{
    final Matrix2DI a,b;
    final Matrix2DI[] alignCosts;

    MotifPositionCost(Matrix2DI a, Matrix2DI b, Matrix2DI[] alignCosts){
        if(a.nrRows != b.nrRows || b.nrRows != alignCosts.length){
            throw new Error("Not the same number of tracks");
        }
        this.a = a;
        this.b = b;
        this.alignCosts = alignCosts;

    }

    @Override
    public float cost(int posA, int posB) {
        int cost = 0;
        for(int track = 0; track < a.nrRows; track++){
            cost+=alignCosts[track].get(a.get(track,posA), b.get(track,posB));
        }
        return cost;
    }
}
