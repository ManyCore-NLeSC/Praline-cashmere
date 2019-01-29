package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.AffineGapCost;

import java.util.HashMap;

public class AlignmentTreeQueue {
    private final int nrLeaves;
    private final AlignmentMode alignmentMode;
    private final Matrix2DF[] costMatrices;
    private final AffineGapCost affineGapCost;
    private final HashMap<Integer, int [][]> sequences;
    private final AlignmentTreeInteger alignmentTreeInteger;
    public long start;

    public AlignmentTreeQueue(int nrLeaves, AlignmentMode mode, Matrix2DF[] costMatrices, Float costStartGap,
                              Float costExtendGap, AlignmentTreeInteger alignmentTreeInteger) {
        this.nrLeaves = nrLeaves;
        this.alignmentMode = mode;
        this.costMatrices = costMatrices;
        affineGapCost = new AffineGapCost(costStartGap, costExtendGap);
        sequences = new HashMap<>();
        this.alignmentTreeInteger = alignmentTreeInteger;
    }

    public Matrix2DF [] getCostMatrices() {
        return costMatrices;
    }

    public AffineGapCost getAffineGapCost() {
        return affineGapCost;
    }

    public AlignmentMode getAlignmentMode() {
        return alignmentMode;
    }

    public TreeAligner getAligner() {
        return new TreeAligner(this);
    }

    public void addElement(int leaf, int [][] sequence) {
        sequences.put(leaf, sequence);
        /*if ( sequences.size() == nrLeaves ) {
            thread = new TreeAligner(this);
            start = System.currentTimeMillis();
            thread.start();
        }*/
    }



    AlignmentTree getTree(){
        return getTree(alignmentTreeInteger);
    }

    AlignmentTree getTree(AlignmentTreeInteger node){
        if(node.sequence == null){
            return new AlignmentTree(getTree(node.left),getTree(node.right));
        } else {
            return new AlignmentTree(new Matrix2DI(sequences.get(node.sequence)));
        }

    }
}
