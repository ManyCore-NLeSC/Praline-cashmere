package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.*;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.IGapCost;

import java.util.Random;
import java.util.Vector;

public class TestMSA {

    static final Matrix2DF testScoreMatrix = new Matrix2DF(new float[][] {
            new float[] {10f, 0f, -2f },
            new float[] {0 , 6f, -4f},
            new float[] {-2, -4, 2f} });

    static final Matrix2DF[] tstScoreMts = new Matrix2DF[]{testScoreMatrix};
    static final IGapCost gaps = new AffineGapCost(-2,-1);
    static final Matrix2DI sequence = new Matrix2DI(5,1);

    static {
        for(int i = 0 ; i < sequence.nrRows; i++){
            sequence.set(i,0,i % 3);
        }
    }

    static void fishForError(int nrSeqs, int minLen, int maxLen){
        Vector<AlignmentTree> res = new Vector<>();
        Random rnd = new Random();
        for(int i = 0 ; i < nrSeqs ; i++){
            int size = rnd.nextInt(maxLen - minLen + 1) + minLen;
            Matrix2DI seq = new Matrix2DI(size,1);
            for(int p = 0 ; p < seq.nrRows ; p++){
                seq.set(p,0,rnd.nextInt(3));
            }
            res.add(new AlignmentTree(seq));
        }
        while(res.size() > 1){
//            System.out.println(res.size());
            Vector<AlignmentTree> rs = new Vector<>();
            for(int i = 0 ; i < res.size() ; i+=2){
                if(i == res.size()-1){
                    rs.add(res.get(i));
                } else {
                    rs.add(new AlignmentTree(res.get(i),res.get(i+1)));
                }
            }
            res = rs;
        }
        MSATree r = new MSA(tstScoreMts,gaps,gaps,AlignmentMode.GLOBAL).msa(res.get(0));
//        r.coordinates.printMatrix();
    }

    void testSimple(){
        AlignmentTree tree = new AlignmentTree(
                new AlignmentTree(new AlignmentTree(sequence), new AlignmentTree(sequence)),
                new AlignmentTree(sequence)
        );
        MSA msa = new MSA(tstScoreMts,gaps,gaps, AlignmentMode.GLOBAL);
        MSATree out = msa.msa(tree);
        Matrix2DI s = out.coordinates;
    }

    public static void main(String[] argv){
        while(true)
        fishForError(10,10,50);
    }
}
