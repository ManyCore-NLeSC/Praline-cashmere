package nl.esciencecenter.praline.data;

import nl.esciencecenter.praline.aligners.ComputeScore;

import java.io.Serializable;

public class MSATree implements Serializable {
    public final Matrix2DI leaf;
    public final MSATree left, right;
    public final Matrix2DF[] prof;
    public final AlignResult res;
    public final Matrix2DI coordinates;

    public MSATree(Matrix2DI leaf){
        this.leaf = leaf;
        this.left = null;
        this.right = null;
        this.res = null;
        this.coordinates = null;
        this.prof =null;
    }

    public MSATree(MSATree left, MSATree right, Matrix2DF[] prof, AlignResult res, Matrix2DI coordinates){
        this.left = left;
        this.right = right;
        this.res = res;
        this.prof = prof;
        this.coordinates = coordinates;
        this.leaf = null;
    }

    public void print(){
        if(left != null) {
            left.print();
        }
        if(right != null){
            right.print();
        }
        if(res != null){
            System.out.println(res.getScore());
        }


    }

}
