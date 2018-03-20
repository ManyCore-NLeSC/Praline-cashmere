package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.aligners.AffineGapAlignerCostOnly;
import nl.esciencecenter.praline.positioncost.PositionCostFromCostMatrices;

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
        AffineGapAlignerCostOnly affineAlignCost = new AffineGapAlignerCostOnly();
        synchronized (results){
            int size = previousElements.size();
            results.addElement(size,size,0f);
        }

        for ( int targetSequence = 0; targetSequence < previousElements.size(); targetSequence++ ) {
            int[][] seqB = previousElements.get(targetSequence);
            Float cost = affineAlignCost.alignCost(sequence.length, seqB.length,
                    parent.getAffineGapCost(),
                    parent.getAffineGapCost(), PositionCostFromCostMatrices.getPosCost(
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
