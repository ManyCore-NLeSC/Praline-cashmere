package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.aligners.MSA;
import nl.esciencecenter.praline.data.AlignmentTree;
import nl.esciencecenter.praline.data.MSATree;

public class TreeAligner  {
    private final AlignmentTree tree;
    private final MSA aligner;

    public TreeAligner(AlignmentTreeQueue treeQueue) {
        tree = treeQueue.getTree();
        aligner = new MSA(treeQueue.getCostMatrices(), treeQueue.getAffineGapCost(), treeQueue.getAffineGapCost(),
            treeQueue.getAlignmentMode());
    }

    public MSATree run() {
        return aligner.msa(tree);
    }
}
