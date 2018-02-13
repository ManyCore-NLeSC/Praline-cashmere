package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix2DF;

public class EasyInterface {

    public AlignResultSteps computeAlignment(Matrix2DF[] profileA, Matrix2DF[] profileB,
                                 Matrix2DF[] costMatrix,
                                 float costStartGap, float costExtendGap, AlignmentMode mode){
        IGapCost gapA = new AffineGapCost(costStartGap, costExtendGap);
        IPositionCost posCost = new MotifProfilePositionCost(profileA,profileB,costMatrix);
        AlignResult res = new AffineAlign().align(profileA.length, profileB.length, gapA, gapA, posCost, mode);
        return new AlignResultSteps(res);
    }
}
