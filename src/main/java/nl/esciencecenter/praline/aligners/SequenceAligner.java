package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.SequenceAlignmentQueue;
import nl.esciencecenter.praline.data.SequenceAlignmentResults;
import nl.esciencecenter.praline.integeralign.AffineAlignCost;
import nl.esciencecenter.praline.integeralign.EasyCostMatrixInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SequenceAligner extends Thread {


    private final int[][] sequence;
    private final List<int[][]> previousElements;
    private final SequenceAlignmentResults results;
    private final SequenceAlignmentQueue parent;


    public SequenceAligner(int[][] sequence, List<int[][]> previousElements, SequenceAlignmentQueue parent, SequenceAlignmentResults results) {
        this.sequence = sequence;
        this.previousElements = previousElements;
        this.results = results;
        this.parent = parent;
    }

    @Override
    public void run() {
        AffineAlignCost affineAlignCost = new AffineAlignCost();
        synchronized (results){
            int size = previousElements.size();
            results.addElement(size,size,0f);
        }

        for ( int targetSequence = 0; targetSequence < previousElements.size(); targetSequence++ ) {
            int[][] seqB = previousElements.get(targetSequence);
            Float cost = affineAlignCost.alignCost(sequence.length, seqB.length,
                    parent.getAffineGapCost(),
                    parent.getAffineGapCost(), EasyCostMatrixInterface.getPosCost(
                            sequence, seqB,
                            parent.getCostMatrices()),
                    parent.getAlignmentMode());
            synchronized (results){
                int size = previousElements.size();
                results.addElement(size,targetSequence,cost);
                results.addElement(targetSequence,size,cost);
            }

        }
    }
}
