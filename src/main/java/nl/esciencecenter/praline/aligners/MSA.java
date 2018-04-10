package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.MotifPositionCost;
import nl.esciencecenter.praline.positioncost.MotifProfilePositionCost;

import java.util.Iterator;
import java.util.List;

public class MSA {
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

    MSATree msa(AlignmentTree tree ){
        if(tree.sequence !=null){
            return new MSATree(tree.sequence);
        }
        MSATree left = msa(tree.left);
        MSATree right = msa(tree.right);
        AlignResult res;
        Matrix2DF[] leftProf;
        Matrix2DI leftSteps;
        if (left.prof == null) {
            leftProf = sequenceToProfile(left.leaf );
            leftSteps = new Matrix2DI(left.leaf.nrRows,1);
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
            rightProf = sequenceToProfile(right.leaf );
            rightSteps = new Matrix2DI(right.leaf.nrRows,1);
            for(int i = 0 ; i < leftSteps.nrRows; i++) {
                rightSteps.set(i, 0, i);
            }
        } else {
            rightProf = right.prof;
            rightSteps = right.coordinates;
        }
        if(left.prof == null && right.prof == null){
            res = new AffineGapAligner().align(left.leaf.nrRows, right.leaf.nrRows,
                    gapCostAg, gapCostBg,
                    new MotifPositionCost(left.leaf, right.leaf, costMatrices), mode);
        } else {
            res = new AffineGapAligner().align(leftProf[0].nrRows, rightProf[0].nrRows,
                    gapCostAg, gapCostBg,
                    new MotifProfilePositionCost(leftProf, rightProf, costMatrices), mode);
        }
        Matrix2DI steps = mergeSteps(leftSteps,rightSteps,res.getSteps());


    }


    Matrix2DI mergeSteps(Matrix2DI a, Matrix2DI b,List<Coordinate> steps){
        assert a.nrRows == b.nrRows;
        Matrix2DI res = new Matrix2DI(a.nrRows, steps.size());
        for(int i = 0 ; i < steps.size() ; i++){
            int x = steps.get(i).getX();
            for(int j = 0 ; j < a.nrCols ; j++){
                res.set(i,j,a.get(x,j));
            }
            int y = steps.get(i).getY();
            for(int j = 0 ; j < b.nrCols; j++){
                res.set(i,j+b.nrCols, b.get(y,j));
            }
        }
        return res;
    }


    Matrix2DF[] mergeProfile(Matrix2DF[] profA, Matrix2DF[] profB, List<Coordinate> steps){
        assert profA.length == profB.length;
        Iterator<Coordinate> it = steps.iterator();
        Matrix2DF[] result = new Matrix2DF(steps.size()-1, profA.nrCols, profA.nrTracks);
        if(!it.hasNext()){
            throw new Error("Not enough elements in alignment for merge profile");
        }
        Coordinate prev = it.next();
        int j = 0;
        while(it.hasNext()){
            Coordinate nxt = it.next();
            int xdiff = nxt.getX() - prev.getX();
            int ydiff = nxt.getY() - prev.getY();
            if(xdiff == 0 && ydiff == 1) {

                for (int i = 0; i < profB.nrCols; i++) {
                    for(int t = 0 ; t < profB.nrTracks; t++){

                        result.set(j, i, t, profB.get(nxt.getY(), i, t));
                    }
                }
            } else if( xdiff == 1 && ydiff == 0) {
                for (int i = 0; i < profB.nrCols; i++) {
                    for(int t = 0 ; t < profB.nrTracks; t++) {
                        result.set(j, i, t, profA.get(nxt.getY(), i, t));
                    }
                }
            } else if (xdiff == 1 && ydiff == 1) {
                for (int i = 0; i < profB.nrCols; i++) {
                    for(int t = 0 ; t < profB.nrTracks; t++) {
                        result.set(j, i,t,
                                0.5f * (profA.get(nxt.getY(), i,t) + profB.get(nxt.getY(), i,t)));
                    }
                }
            } else {
                throw new Error("Non-adjacent coordinates");
            }
        }
        return result;
    }


    Matrix2DF[] sequenceToProfile(Matrix2DI a){
        // initialize to zero
        Matrix2DF[] profiles = new Matrix2DF[a.nrCols];
        for(int i = 0 ; i < profiles.length; i++){
            profiles[i] = new Matrix2DF(a.nrRows,costMatrices[i].nrCols);
        }
        for(int pos = 0 ; pos < a.nrRows ; pos++){
            for(int t = 0 ; t < a.nrCols; t++){
                profiles[t].set(pos,a.get(pos,t),1.0f);
            }
        }
        return profiles;
    }
}
