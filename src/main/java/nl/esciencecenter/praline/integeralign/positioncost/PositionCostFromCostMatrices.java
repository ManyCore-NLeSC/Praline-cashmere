package nl.esciencecenter.praline.integeralign.positioncost;

import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.integeralign.positioncost.IPositionCost;

public class PositionCostFromCostMatrices {


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
