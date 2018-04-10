package nl.esciencecenter.praline.network;

class AlignmentTreeInteger {
    public final AlignmentTreeInteger left;
    public final AlignmentTreeInteger right;
    public final Integer sequence;

    public AlignmentTreeInteger(AlignmentTreeInteger left, AlignmentTreeInteger right){
        this.left = left;
        this.right = right;
        this.sequence = null;
    }

    public AlignmentTreeInteger(int sequence){
        this.sequence = sequence;
        this.left = null;
        this.right = null;
    }

}
