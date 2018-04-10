package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.aligners.MSA;
import nl.esciencecenter.praline.data.AlignmentTree;
import nl.esciencecenter.praline.data.MSATree;

public class TreeAligner extends Thread {
    private final AlignmentTree tree;
    private final MSA aligner;
    private MSATree result;

    public TreeAligner(AlignmentTreeQueue treeQueue) {
        tree = treeQueue.getTree();
        aligner = new MSA(treeQueue.getCostMatrices(), treeQueue.getAffineGapCost(), treeQueue.getAffineGapCost(),
            treeQueue.getAlignmentMode());
    }

    @Override
    public void run() {
        result = aligner.msa(tree);
    }

    public MSATree getResult() {
        return result;
    }
}
