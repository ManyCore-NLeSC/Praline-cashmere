package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix2DF;

public class EasyInterface {

    AlignResult computeAlignment(Matrix2DF[] profileA, Matrix2DF[] profileB,
                                 Matrix2DF[] costMatrix,
                                 float costStartGapA, float costExtendGapA, float costStartGapB, float costExtendGapB, AlignmentMode mode){
        IGapCost gapA = new AffineGapCost(costStartGapA, costExtendGapA);
        IGapCost gapB = new AffineGapCost(costStartGapB, costExtendGapB);
        IPositionCost posCost = new MotifProfilePositionCost(profileA,profileB,costMatrix);
        return new AffineAlign().align(profileA.length, profileB.length, gapA, gapB, posCost, mode);
    }
}
