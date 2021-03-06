package nl.esciencecenter.praline.aligners;

import java.io.Serializable;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.LinearGapCost;
import nl.esciencecenter.praline.positioncost.MotifPositionCost;
import nl.esciencecenter.praline.positioncost.MotifProfilePositionCost;

public class MSA implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(MSA.class);
    
    final Matrix2DF[] costMatrices;
    final IGapCost gapCostAg;
    final IGapCost gapCostBg;
    final AlignmentMode mode;

    public MSA(Matrix2DF[] costMatrices, IGapCost gapCostAg, IGapCost gapCostBg, AlignmentMode mode) {
        this.costMatrices = costMatrices;
        this.gapCostAg = gapCostAg;
        this.gapCostBg = gapCostBg;
        this.mode = mode;
    }

    AffineGapCost getAffineGapCost(IGapCost iGapCost) {
	AffineGapCost gapCost;
	if(iGapCost instanceof LinearGapCost) {
            int cost = ((LinearGapCost)gapCostAg).cost;
            gapCost = new AffineGapCost(cost,cost);
        } else {
            gapCost = ((AffineGapCost)  gapCostAg);
        }

	return gapCost;
    }

    
    public MSATree msa(AlignmentTree tree ){
        if(tree.sequence !=null){
	    logger.debug("The alignment tree has a sequence, returning it");
            return new MSATree(tree.sequence);
        }


        MSATree left = msa(tree.left);
        MSATree right = msa(tree.right);

	logger.debug("Running MSA");

	AlignResult res;

        Matrix2DF[] leftProf;
        Matrix2DI leftSteps;
	
        if (left.prof == null ) {
	    logger.debug("left.prof == null, so create a profile from sequence");
            leftProf = sequenceToProfile(left.leaf );
            leftSteps = new Matrix2DI(left.leaf.nrCols + 1,1);
            for(int i = 0 ; i < leftSteps.nrRows; i++) {
                leftSteps.set(i, 0, i);
            }
        } else {
	    logger.debug("left.prof != null, so using steps and profile from left");
            leftProf = left.prof;
            leftSteps = left.coordinates;
        }
	
        Matrix2DF[] rightProf;
        Matrix2DI rightSteps;
	
        if (right.prof == null) {
	    logger.debug("right.prof == null, so create a profile from sequence");
            rightProf = sequenceToProfile(right.leaf );
            rightSteps = new Matrix2DI(right.leaf.nrCols + 1,1);
            for(int i = 0 ; i < rightSteps.nrRows; i++) {
                rightSteps.set(i, 0, i);
            }
        } else {
	    logger.debug("right.prof != null, so using steps and profile from right");
            rightProf = right.prof;
            rightSteps = right.coordinates;
        }

	boolean mc = false;
        if(left.prof == null && right.prof == null){
	    logger.debug("Both left.prof and right.prof == null, so applying AffineGapAligner on the leafs");

	    int maxA = 0;
	    int maxB = 0;
	    for (int i = 0; i < costMatrices.length; i++) {
		int nrRows = costMatrices[i].nrRows;
		int nrColumns = costMatrices[i].nrCols;
		logger.debug("Costmatrix {} has {} rows and {} columns", i, nrRows, nrColumns);
		maxA += nrColumns;
		maxB += nrRows;
	    }

	    logger.debug("maxA: {}, maxB: {}", maxA, maxB);

	    // if (mc) {
		// Kernel kernel = Cashmere.getKernel("align");
		// KernelLaunch kl = kernel.createLaunch();
		// MCL.launchAlignKernel(kl, left.leaf.nrCols, right.leaf.nrCols,
		// 	sizeAlphabetMax, // ???
		// 	costMatrices.length, // nrTracks
		// 	sizesAlphabet
	    // }
	    // else {
		logger.debug("launching AffineGapAligner with MotifPositionCost");
		logger.debug("  left.leaf.nrCols: {}", left.leaf.nrCols);
		logger.debug("  right.leaf.nrCols: {}", right.leaf.nrCols);
		logger.debug("  sizeAlphabetMax: {}", 0);
		logger.debug("  nrTracks: {}", costMatrices.length);
		AffineGapCost gapCostA = getAffineGapCost(gapCostAg);
		logger.debug("  gapCostStartA: {}", gapCostA.start);
		logger.debug("  gapCostExtendA: {}", gapCostA.extend);
		AffineGapCost gapCostB = getAffineGapCost(gapCostBg);
		logger.debug("  gapCostStartB: {}", gapCostB.start);
		logger.debug("  gapCostExtendB: {}", gapCostB.extend);
		res = new AffineGapAligner().align(left.leaf.nrCols, right.leaf.nrCols,
			gapCostAg, gapCostBg,
			new MotifPositionCost(left.leaf, right.leaf, costMatrices), mode);
		assert ComputeScore.getAlignScore(res.getAlignSteps(),left.leaf.nrCols, right.leaf.nrCols,gapCostAg, gapCostBg,
			new MotifPositionCost(left.leaf, right.leaf, costMatrices)) == res.getScore();
	    // }

        } else {
	    logger.debug("Applying AffineGapAligner on left- and rightProf");

	    int maxA = 0;
	    int maxB = 0;
	    for (int i = 0; i < costMatrices.length; i++) {
		int nrRows = costMatrices[i].nrRows;
		int nrColumns = costMatrices[i].nrCols;
		logger.debug("Costmatrix {} has {} rows and {} columns", i, nrRows, nrColumns);
		maxA += nrColumns;
		maxB += nrRows;
	    }

	    logger.debug("maxA: {}, maxB: {}", maxA, maxB);

	    
            // if (mc) {
	    // }
	    // else {
		logger.debug("launching AffineGapAligner with MotifProfilePositionCost");
		logger.debug("  leftProf[0].nrRows: {}", leftProf[0].nrRows);
		logger.debug("  rightProf[0].nrCols: {}", rightProf[0].nrRows);
		logger.debug("  sizeAlphabetMax: {}", 0);
		logger.debug("  nrTracks: {}", costMatrices.length);
		AffineGapCost gapCostA = getAffineGapCost(gapCostAg);
		logger.debug("  gapCostStartA: {}", gapCostA.start);
		logger.debug("  gapCostExtendA: {}", gapCostA.extend);
		AffineGapCost gapCostB = getAffineGapCost(gapCostBg);
		logger.debug("  gapCostStartB: {}", gapCostB.start);
		logger.debug("  gapCostExtendB: {}", gapCostB.extend);
		res = new AffineGapAligner().align(leftProf[0].nrRows, rightProf[0].nrRows,
			gapCostAg, gapCostBg,
			new MotifProfilePositionCost(leftProf, rightProf, costMatrices), mode);
		assert ComputeScore.getAlignScore(res.getAlignSteps(),leftProf[0].nrRows, rightProf[0].nrRows,
			gapCostAg, gapCostBg,
			new MotifProfilePositionCost(leftProf, rightProf, costMatrices)) == res.getScore();
	    // }
        }
//        System.out.println("LEFTA");
//        leftSteps.printMatrix();
//        System.out.println("\n");
//        rightSteps.printMatrix();
//        System.out.println("\n");
//        for(Coordinate c : res.getSteps()){
//            System.out.println(c.toString());
//        }
//        System.out.println("\n\n");

//        for(int i= 0 ; i < res.getSteps().size(); i++){
//            System.err.printf("%d %d\n", res.getSteps().get(i).getX(), res.getSteps().get(i).getY());
//        }
//        System.err.printf("Left %d %d Right %d %d\n", leftSteps.nrRows, leftSteps.nrCols, rightSteps.nrRows, rightSteps.nrCols);
	logger.debug("Merging the steps and the profiles");
        Matrix2DI steps = mergeSteps(leftSteps,rightSteps,res.getSteps());
        Matrix2DF[] prof = mergeProfile(leftProf,rightProf,res.getSteps());
//        prof[0].printMatrix();
//        System.out.println("\n");

        return new MSATree(left,right,prof,res,steps);

    }


    Matrix2DI mergeSteps(Matrix2DI a, Matrix2DI b,List<Coordinate> steps){

        //assert a.nrRows == b.nrRows;
        Matrix2DI res = new Matrix2DI(steps.size(),a.nrCols + b.nrCols);
        for(int i = 0 ; i < steps.size() ; i++){

            int x = steps.get(i).getX();
            for(int j = 0 ; j < a.nrCols ; j++){
                res.set(i,j,a.get(x,j));
            }
            int y = steps.get(i).getY();
            for(int j = 0 ; j < b.nrCols; j++){
                res.set(i,a.nrCols + j, b.get(y,j));
            }
        }
        return res;
    }


    Matrix2DF[] mergeProfile(Matrix2DF[] profA, Matrix2DF[] profB, List<Coordinate> steps){
        assert profA.length == profB.length;
        assert profA[0].nrCols == profB[0].nrCols;
        Iterator<Coordinate> it = steps.iterator();
        Matrix2DF[] result = new Matrix2DF[profA.length];
        for(int i = 0 ; i < result.length; i++){
            result[i] = new Matrix2DF(steps.size()-1,profA[i].nrCols);
        }
        if(!it.hasNext()){
            throw new Error("Not enough elements in alignment for merge profile");
        }
        Coordinate prev = it.next();
        int j = 0;
        while(it.hasNext()){
            Coordinate nxt = it.next();
            int xdiff = nxt.getX() - prev.getX();
            int ydiff = nxt.getY() - prev.getY();
            if(xdiff == 1 && ydiff == 0) {
                for(int t = 0 ; t < profA.length; t++){
                    for (int i = 0; i < profA[t].nrCols; i++) {

                        result[t].set(j,i,profA[t].get(nxt.getX()-1,i));
                    }
                }
            } else if( xdiff == 0 && ydiff == 1) {
                for(int t = 0 ; t < profB.length; t++){
                    for (int i = 0; i < profB[t].nrCols; i++) {

                        result[t].set(j,i,profB[t].get(nxt.getY()-1,i));
                    }
                }
            } else if (xdiff == 1 && ydiff == 1) {
                if(profA[0].nrCols != profB[0].nrCols){
                    throw new Error("Nr cols neq " +profA[0].nrCols + " " + profB[0].nrCols );
                }
                for(int t = 0 ; t < profB.length; t++) {
                for (int i = 0; i < profB[t].nrCols; i++) {
                        result[t].set(j,i, 0.5f * (

                                        profB[t].get(nxt.getY()-1,i) +
                                                profA[t].get(nxt.getX()-1,i)));
                    }
                }
            } else {
                System.out.println(nxt.toString());
                System.out.println(prev.toString());
                throw new Error("Non-adjacent coordinates");
            }
            prev = nxt;
            j++;
        }
        return result;
    }


    Matrix2DF[] sequenceToProfile(Matrix2DI a){
        // initialize to zero

        int tracks = a.nrRows;
        if(costMatrices.length != tracks){
            System.err.printf("tracks %d costmatrices %d nrCols %d\n", tracks, costMatrices.length, a.nrCols);
            throw new Error("");
        }
        int nrPos = a.nrCols;
        Matrix2DF[] profiles = new Matrix2DF[tracks];
        for(int i = 0 ; i < tracks; i++){
            profiles[i] = new Matrix2DF(nrPos,costMatrices[i].nrCols);
        }
        for(int t = 0 ; t < tracks; t++){
            for(int pos = 0 ; pos < nrPos ; pos++){
                profiles[t].set(pos,a.get(t,pos),1.0f);
            }
        }

        return profiles;
    }
}
