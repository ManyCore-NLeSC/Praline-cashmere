package nl.esciencecenter.praline.positioncost;

import nl.esciencecenter.praline.data.Matrix2DF;

public class PositionCostFromCostMatrices {

    // sequences are two dimensional, first dimension: track, second dimension: position
    // cost matrix per track
    public static IPositionCost getPosCost(int [][] sequenceOne, int [][] sequenceTwo, Matrix2DF[] costMatrices){
        return new IPositionCost() {
            @Override
            public float cost(int posA, int posB) {
                float cost = 0;
                for(int x = 0 ; x < costMatrices.length ; x++){
                    cost+=costMatrices[x].get(sequenceOne[posA][x], sequenceTwo[posB][x]);
                }
                return cost;
            }
        };
    }


}
