package nl.esciencecenter.praline.data;

import nl.esciencecenter.praline.integeralign.AlignmentMode;

import java.util.ArrayList;
import java.util.List;

public class SequenceAlignmentQueue {
    private String name;
    private Matrix2DF [] costMatrices;
    private AlignmentMode alignmentMode;
    private ArrayList<int [][]> queue;

    public SequenceAlignmentQueue(String name, AlignmentMode alignmentMode) {
        this.name = name;
        this.alignmentMode = alignmentMode;
        queue = new ArrayList<>();
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
}
