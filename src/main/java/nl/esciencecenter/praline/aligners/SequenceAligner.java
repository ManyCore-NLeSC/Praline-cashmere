package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.SequenceAlignmentQueue;
import nl.esciencecenter.praline.data.SequenceAlignments;
import nl.esciencecenter.praline.integeralign.AffineAlignCost;
import nl.esciencecenter.praline.integeralign.EasyCostMatrixInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SequenceAligner extends Thread {
    private final String name;
    private final HashMap<String, ReentrantLock> locks;
    private final HashMap<String, SequenceAlignmentQueue> sequenceAlignmentQueue;
    private final HashMap<String, SequenceAlignments> sequenceAlignments;
    private final AffineAlignCost affineAlignCost;

    public SequenceAligner(String name, HashMap<String, ReentrantLock> locks,
                           HashMap<String, SequenceAlignmentQueue> sequenceAlignmentQueue,
                           HashMap<String, SequenceAlignments> sequenceAlignments) {
        this.name = name;
        this.locks = locks;
        this.sequenceAlignmentQueue = sequenceAlignmentQueue;
        this.sequenceAlignments = sequenceAlignments;
        affineAlignCost = new AffineAlignCost();
    }

    @Override
    public void run() {
        synchronized ( locks.get("sequence_alignments") ) {
            int [][] sequence = sequenceAlignmentQueue.get(name).getLastElement();
            ArrayList<int [][]> targetSequences = new ArrayList<int[][]>(
                    (sequenceAlignmentQueue.get(name).getPreviousElements()));
            sequenceAlignments.get(name).addElement(targetSequences.size(), targetSequences.size(), 0f);
            for ( int targetSequence = 0; targetSequence < targetSequences.size(); targetSequence++ ) {
                int[][] seqB = targetSequences.get(targetSequence);
                Float cost = affineAlignCost.alignCost(sequence.length, seqB.length,
                        sequenceAlignmentQueue.get(name).getAffineGapCost(),
                        sequenceAlignmentQueue.get(name).getAffineGapCost(), EasyCostMatrixInterface.getPosCost(
                                sequence, seqB,
                                sequenceAlignmentQueue.get(name).getCostMatrices()),
                        sequenceAlignmentQueue.get(name).getAlignmentMode());
                sequenceAlignments.get(name).addElement(targetSequences.size(), targetSequence, cost);
                sequenceAlignments.get(name).addElement(targetSequence, targetSequences.size(), cost);
            }
            locks.get("sequence_alignments").notifyAll();
            System.err.println("DONE!");
        }
    }
}
