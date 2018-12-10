package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.MotifPositionCost;
import nl.esciencecenter.praline.positioncost.MotifProfilePositionCost;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MSA implements Serializable {
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

    public MSATree msa(AlignmentTree tree ){
        if(tree.sequence !=null){
            return new MSATree(tree.sequence);
        }

        MSATree left = msa(tree.left);
        MSATree right = msa(tree.right);
        AlignResult res;
        Matrix2DF[] leftProf;
        Matrix2DI leftSteps;
        if (left.prof == null ) {
            // prepare profile for mergeProfile
            leftProf = sequenceToProfile(left.leaf );
            leftSteps = new Matrix2DI(left.leaf.nrCols + 1,1);
            // Prepare 1 dimensional align path for mergeSteps
            for(int i = 0 ; i < leftSteps.nrRows; i++) {
                leftSteps.set(i, 0, i);
            }
        } else {
            leftProf = left.prof;
            leftSteps = left.coordinates;
        }
        Matrix2DF[] rightProf;
        Matrix2DI rightSteps;
        if (right.prof == null) {
            // prepare profile for mergeProfile
            rightProf = sequenceToProfile(right.leaf );
            // Prepare 1 dimensional align path for mergeSteps
            rightSteps = new Matrix2DI(right.leaf.nrCols + 1,1);
            for(int i = 0 ; i < rightSteps.nrRows; i++) {
                rightSteps.set(i, 0, i);
            }
        } else {
            rightProf = right.prof;
            rightSteps = right.coordinates;
        }

        // if left and right nodes are leaves we can
        // use the more efficient non-profile aligner
        if(left.prof == null && right.prof == null){
            res = new AffineGapAligner().align(left.leaf.nrCols, right.leaf.nrCols,
                    gapCostAg, gapCostBg,
                    new MotifPositionCost(left.leaf, right.leaf, costMatrices), mode);
            //assert ComputeScore.getAlignScore(res.getAlignSteps(),left.leaf.nrCols, right.leaf.nrCols,gapCostAg, gapCostBg,
            //       new MotifPositionCost(left.leaf, right.leaf, costMatrices)) == res.getScore();

        } else {

            res = new AffineGapAligner().align(leftProf[0].nrRows, rightProf[0].nrRows,
                    gapCostAg, gapCostBg,
                    new MotifProfilePositionCost(leftProf, rightProf, costMatrices), mode);
            //assert ComputeScore.getAlignScore(res.getAlignSteps(),leftProf[0].nrRows, rightProf[0].nrRows,
            //        gapCostAg, gapCostBg,
            //        new MotifProfilePositionCost(leftProf, rightProf, costMatrices)) == res.getScore();
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
        Matrix2DI steps = mergeSteps(leftSteps,rightSteps,res.getSteps());
        Matrix2DF[] prof = mergeProfile(leftProf,rightProf,res.getSteps());
//        prof[0].printMatrix();
//        System.out.println("\n");



        return new MSATree(left,right,prof,res,steps);

    }

    
    Matrix2DI mergeSteps(Matrix2DI stepsLeft, Matrix2DI stepsRight,List<Coordinate> stepsCurrent){

        //assert a.nrRows == b.nrRows;
        //
        // vertical = steps, horizontal= n-dimensional coordinate
        // stepsLeft = k-dimensional coordinate steps, length a
        // stepsRight = l-dimensional coordinate steps, length b
        // stepsCurrent = 2-dimensional coordinate steps, length n,
        //        (first coordinate is in range (0,a) , second in range (0,b))
        // result = (k+l)-dimensional coordinate steps
        Matrix2DI res = new Matrix2DI(stepsCurrent.size(),stepsLeft.nrCols + stepsRight.nrCols);
        for(int i = 0 ; i < stepsCurrent.size() ; i++){

            int x = stepsCurrent.get(i).getX();
            for(int j = 0 ; j < stepsLeft.nrCols ; j++){
                res.set(i,j,stepsLeft.get(x,j));
            }
            int y = stepsCurrent.get(i).getY();
            for(int j = 0 ; j < stepsRight.nrCols; j++){
                res.set(i,stepsLeft.nrCols + j, stepsRight.get(y,j));
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
            // gap in B, copy from A
            if(xdiff == 1 && ydiff == 0) {
                for(int t = 0 ; t < profA.length; t++){
                    for (int i = 0; i < profA[t].nrCols; i++) {

                        result[t].set(j,i,profA[t].get(nxt.getX()-1,i));
                    }
                }
            // gap in A, copy from B
            } else if( xdiff == 0 && ydiff == 1) {
                for(int t = 0 ; t < profB.length; t++){
                    for (int i = 0; i < profB[t].nrCols; i++) {

                        result[t].set(j,i,profB[t].get(nxt.getY()-1,i));
                    }
                }
            // align, average A and B
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
            // should not happen
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
