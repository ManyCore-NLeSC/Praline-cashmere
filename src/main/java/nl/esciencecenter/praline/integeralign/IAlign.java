package nl.esciencecenter.praline.integeralign;



public interface IAlign {

    AlignResult align(int sizeA, int sizeB, IGapCost gapCostA,  IGapCost gapCostB, IPositionCost costs, AlignmentMode mode);
}
