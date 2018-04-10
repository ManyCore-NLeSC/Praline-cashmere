package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.gapcost.AffineGapCost;

import java.util.ArrayList;

public class AlignmentTreeQueue {
    private final int nrLeaves;
    private final AlignmentMode alignmentMode;
    private final Matrix2DF[] costMatrices;
    private final AffineGapCost affineGapCost;
    private final ArrayList<int [][]> sequences;
    private final AlignmentTreeInteger alignmentTreeInteger;

    public AlignmentTreeQueue(int nrLeaves, AlignmentMode mode, Matrix2DF[] costMatrices, Float costStartGap,
                              Float costExtendGap, AlignmentTreeInteger alignmentTreeInteger) {
        this.nrLeaves = nrLeaves;
        this.alignmentMode = mode;
        this.costMatrices = costMatrices;
        affineGapCost = new AffineGapCost(costStartGap, costExtendGap);
        sequences = new ArrayList<>();
        this.alignmentTreeInteger = alignmentTreeInteger;
    }
}
