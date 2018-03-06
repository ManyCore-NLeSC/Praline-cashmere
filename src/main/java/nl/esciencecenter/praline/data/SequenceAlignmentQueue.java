package nl.esciencecenter.praline.data;

import nl.esciencecenter.praline.integeralign.AffineGapCost;
import nl.esciencecenter.praline.integeralign.AlignmentMode;

import java.util.ArrayList;
import java.util.List;

public class SequenceAlignmentQueue {
    private String name;
    private AffineGapCost affineGapCost;
    private Matrix2DF [] costMatrices;
    private AlignmentMode alignmentMode;
    private ArrayList<int [][]> queue;

    public SequenceAlignmentQueue(String name, AlignmentMode alignmentMode) {
        this.name = name;
        this.alignmentMode = alignmentMode;
        queue = new ArrayList<>();
    }

    public void setGapCost(Float costStartGap, Float costExtendGap) {
        affineGapCost = new AffineGapCost(costStartGap, costExtendGap);
    }

    public AffineGapCost getAffineGapCost() {
        return affineGapCost;
    }

    public void setCostMatrices(Matrix2DF [] costMatrices) {
        this.costMatrices = costMatrices;
    }

    public Matrix2DF[] getCostMatrices() {
        return costMatrices;
    }

    public AlignmentMode getAlignmentMode() {
        return alignmentMode;
    }

    public void addElement(int [][] sequence) {
        queue.add(sequence);
    }

    public List<int [][]> getPreviousElements() {
        return queue.subList(0, queue.size() - 1);
    }

    public int [][] getLastElement() {
        return queue.get(queue.size() - 1);
    }
}
