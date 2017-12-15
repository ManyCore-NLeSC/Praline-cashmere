package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.PositionCost;

import java.util.List;

public interface IAlign {

    AlignResult align(int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost costs);
}
