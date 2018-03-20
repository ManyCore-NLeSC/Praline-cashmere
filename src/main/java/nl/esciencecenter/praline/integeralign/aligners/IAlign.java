package nl.esciencecenter.praline.integeralign.aligners;


import nl.esciencecenter.praline.integeralign.AlignResult;
import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.integeralign.gapcost.IGapCost;
import nl.esciencecenter.praline.integeralign.positioncost.IPositionCost;

public interface IAlign {

    AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost costs, AlignmentMode mode);
}
