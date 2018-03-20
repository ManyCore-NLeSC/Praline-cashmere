package nl.esciencecenter.praline.aligners;


import nl.esciencecenter.praline.data.AlignResult;
import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

public interface IAlign {

    AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost costs, AlignmentMode mode);
}
