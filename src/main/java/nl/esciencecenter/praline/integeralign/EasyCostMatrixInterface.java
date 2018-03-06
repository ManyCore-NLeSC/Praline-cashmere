package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix2DF;

public class EasyCostMatrixInterface {

    public static IPositionCost getPosCost(int[][][] sequences,
                             Matrix2DF[] costMatrices, int i, int j ){
        return new IPositionCost() {
            @Override
            public float cost(int posA, int posB) {
                float cost = 0;
                for(int x = 0 ; x < costMatrices.length ; x++){
                    cost+=costMatrices[x].get(sequences[i][posA][x], sequences[j][posB][x]);
                }
                return cost;
            }
        };
    }

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

    public Matrix2DF computeAlignment(int[][][] sequences,
                                             Matrix2DF[] costMatrices,
                                             float costStartGap, float costExtendGap, AlignmentMode mode){
        assert sequences[0][0].length == costMatrices.length;
        Matrix2DF res = new Matrix2DF(sequences.length, sequences.length);
        for(int i = 0 ; i < sequences.length; i++){
            res.set(i,i,0);
        }
        AffineAlignCost af = new AffineAlignCost();
        AffineGapCost cost = new AffineGapCost(costStartGap, costExtendGap);
        for(int i = 0 ; i < sequences.length ; i++){
            for(int j = i + 1 ; j < sequences.length ; j++){
                float c = af.alignCost(sequences[i].length, sequences[j].length, cost, cost , getPosCost(sequences, costMatrices, i, j), mode);
                res.set(i,j,c);
                res.set(j,i,c);
            }
        }
        return res;
    }

}
