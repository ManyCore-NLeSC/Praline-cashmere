package nl.esciencecenter.praline.positioncost;

import nl.esciencecenter.praline.data.Matrix2DF;

public class MotifProfilePositionCost implements IPositionCost {

    final ProfilePositionCost[] costs;

    // matrix for each track, cost matrix also for each track
    public MotifProfilePositionCost(Matrix2DF[] profileA, Matrix2DF[] profileB, Matrix2DF[] costMatrix) {
        assert profileA.length == profileB.length && profileA.length == costMatrix.length;
        costs = new ProfilePositionCost[profileA.length];
        for(int i = 0 ; i < profileA.length ; i++){
            costs[i] = new ProfilePositionCost(profileA[i], profileB[i],costMatrix[i]);
        }
    }

    @Override
    public float cost(int posA, int posB) {
        float cost = 0;
        for(int track = 0; track < costs.length; track++){
            cost+=costs[track].cost(posA,posB);
        }
        return cost;
    }
}
