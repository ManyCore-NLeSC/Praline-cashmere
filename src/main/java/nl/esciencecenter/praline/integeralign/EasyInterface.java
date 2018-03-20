package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.integeralign.aligners.AffineGapAligner;
import nl.esciencecenter.praline.integeralign.gapcost.AffineGapCost;
import nl.esciencecenter.praline.integeralign.gapcost.IGapCost;
import nl.esciencecenter.praline.integeralign.positioncost.IPositionCost;
import nl.esciencecenter.praline.integeralign.positioncost.MotifProfilePositionCost;

public class EasyInterface {

    public AlignResultSteps computeAlignment(Matrix2DF[] profileA, Matrix2DF[] profileB,
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
        AlignResult res = new AffineGapAligner().align(alength, blength, gapA, gapA, posCost, mode);
        return new AlignResultSteps(res);
    }
}
