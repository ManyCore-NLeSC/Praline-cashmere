package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.aligners.MSA;
import nl.esciencecenter.praline.data.AlignmentTree;
import nl.esciencecenter.praline.data.MSATree;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeAligner implements Serializable{
    
    final static Logger logger = LoggerFactory.getLogger(TreeAligner.class);
    
    private final AlignmentTree tree;
    private final MSA aligner;

    public TreeAligner(AlignmentTreeQueue treeQueue) {
	logger.debug("Creating a new TreeAligner with MSA as alignmer");
        tree = treeQueue.getTree();
        aligner = new MSA(treeQueue.getCostMatrices(), treeQueue.getAffineGapCost(), treeQueue.getAffineGapCost(),
            treeQueue.getAlignmentMode());
    }

    public MSATree run() {
	logger.debug("Running the MSA aligner");
        return aligner.msa(tree);
    }
}
