package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.integeralign.gapcost.AffineGapCost;
import nl.esciencecenter.praline.data.AlignmentMode;

import java.util.ArrayList;
import java.util.HashSet;

public class SequenceAlignmentQueue {
    private final AffineGapCost affineGapCost;
    private final Matrix2DF[] costMatrices;
    private final AlignmentMode alignmentMode;
    private final ArrayList<int [][]> queue;
    private final HashSet<SequenceAligner> sequenceAlignerThreads;
    private final SequenceAlignmentResults results;

    public SequenceAlignmentQueue(AlignmentMode mode, Matrix2DF[] costMatrices, Float costStartGap, Float costExtendGap) {
        this.alignmentMode = mode;
        this.costMatrices = costMatrices;
        affineGapCost = new AffineGapCost(costStartGap, costExtendGap);
        this.sequenceAlignerThreads = new HashSet<>();
        queue = new ArrayList<>();
        results = new SequenceAlignmentResults();
    }


    public AffineGapCost getAffineGapCost() {
        return affineGapCost;
    }

    public Matrix2DF[] getCostMatrices() {
        return costMatrices;
    }

    public AlignmentMode getAlignmentMode() {
        return alignmentMode;
    }

    public int size(){
        return queue.size();
    }

    public void addElement(int [][] sequence) {
        synchronized(queue) {
            queue.add(sequence);
            SequenceAligner newThread = new SequenceAligner(sequence, getPreviousElements(), this, results);
            synchronized (sequenceAlignerThreads) {
                sequenceAlignerThreads.add(newThread);
            }
            newThread.start();
        }
    }

    public ArrayList<int [][]> getPreviousElements() {
        return new ArrayList<int[][]>(queue.subList(0, queue.size() - 1));
    }

    public int [][] getLastElement() {
        return queue.get(queue.size() - 1);
    }

    public void waitForResult() {
        synchronized (sequenceAlignerThreads){
            for(SequenceAligner s : sequenceAlignerThreads){
                try {
                    s.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getScoreMatrixString() {
        synchronized (results) {
            return results.toString();
        }
    }
}
