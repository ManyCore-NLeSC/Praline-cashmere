package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix2DF;

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
        //profileA[0].printMatrix();
        int alength = profileA[0].nrRows;
        int blength = profileB[0].nrRows;
        IGapCost gapA = new AffineGapCost(costStartGap, costExtendGap);
        IPositionCost posCost = new MotifProfilePositionCost(profileA,profileB,costMatrix);
        AlignResult res = new AffineAlign().align(alength, blength, gapA, gapA, posCost, mode);
        return new AlignResultSteps(res);
    }
}
