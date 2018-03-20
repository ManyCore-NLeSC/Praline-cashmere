package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.AlignResult;
import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.aligners.AffineGapAligner;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;
import nl.esciencecenter.praline.positioncost.MotifProfilePositionCost;

public class AffineMotifProfileAlignInterface {

    public AlignResult computeAlignment(Matrix2DF[] profileA, Matrix2DF[] profileB,
                                        Matrix2DF[] costMatrix,
                                        float costStartGap, float costExtendGap, AlignmentMode mode){
        assert profileA.length == profileB.length && profileB.length == costMatrix.length;
        for(int i = 0 ; i < profileA.length; i++){
            System.out.println(i);
            System.out.printf("A rows %d cols %d\n", profileA[i].nrRows, profileA[i].nrCols);
            System.out.printf("B rows %d cols %d\n", profileB[i].nrRows, profileB[i].nrCols);
            System.out.printf("C rows %d cols %d\n", costMatrix[i].nrRows, costMatrix[i].nrCols);
        }
//        System.out.println("Cost:");
//        costMatrix[0].printMatrix();
//        System.out.println("A:");
//        profileA[0].printMatrix();
//        System.out.println("B:");
//        profileB[0].printMatrix();
        int alength = profileA[0].nrRows;
        int blength = profileB[0].nrRows;
        IGapCost gapA = new AffineGapCost(costStartGap, costExtendGap);
        IPositionCost posCost = new MotifProfilePositionCost(profileA,profileB,costMatrix);
        return new AffineGapAligner().align(alength, blength, gapA, gapA, posCost, mode);
    }
}
