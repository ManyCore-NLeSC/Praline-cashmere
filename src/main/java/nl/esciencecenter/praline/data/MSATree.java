package nl.esciencecenter.praline.data;

public class MSATree {
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
}
