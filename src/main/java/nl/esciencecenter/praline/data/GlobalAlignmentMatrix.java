package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class GlobalAlignmentMatrix extends AlignmentMatrix {

    public GlobalAlignmentMatrix(String id, Sequence seqA, Sequence seqB) {
        super(id,seqA,seqB);
    }

    public float getScore() {
        return getScore(getSeqA().getLength(), getSeqB().getLength());
    }


    public ArrayList<String> getAlignment( ) {
        return getAlignment(getSeqA().getLength(),getSeqB().getLength());
    }

}
