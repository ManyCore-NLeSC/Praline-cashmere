package nl.esciencecenter.praline.data;


public class AlignmentTree {
    public final AlignmentTree left;
    public final AlignmentTree right;
    public final Matrix2DI sequence;

    public AlignmentTree(AlignmentTree left, AlignmentTree right){
        this.left = left;
        this.right = right;
        this.sequence = null;
    }

    public AlignmentTree(Matrix2DI sequence){
        this.sequence = sequence;
        this.left = null;
        this.right = null;
    }

}
