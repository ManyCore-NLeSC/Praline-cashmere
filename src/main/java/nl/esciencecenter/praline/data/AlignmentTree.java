package nl.esciencecenter.praline.data;

import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

public class AlignmentTree {
    final AlignmentTree left;
    final AlignmentTree right;
    final Integer sequence;

    public AlignmentTree(AlignmentTree left, AlignmentTree right){
        this.left = left;
        this.right = right;
        this.sequence = null;
    }

    public AlignmentTree(int sequence){
        this.sequence = sequence;
        this.left = null;
        this.right = null;
    }

}
