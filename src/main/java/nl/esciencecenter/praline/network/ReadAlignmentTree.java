package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.AlignmentTree;
import nl.esciencecenter.praline.data.Matrix2DI;

import java.util.Vector;

public class ReadAlignmentTree {



    public static AlignmentTreeInteger readTree(int nrLeafs, String s){
        Vector<AlignmentTreeInteger> treeArr = new Vector<>();
        for(int i = 0 ; i < nrLeafs; i++){
            treeArr.add(new AlignmentTreeInteger(i));
        }
        String[] ss = s.split(" ");
        AlignmentTreeInteger res = null;
        for(String v : ss){
            String[] mv = v.split(",");
            int merge = Integer.parseInt(mv[0]);
            int with = Integer.parseInt(mv[1]);
            res = new AlignmentTreeInteger(treeArr.get(merge), treeArr.get(with));
            treeArr.set(merge,res);
            treeArr.remove(with);
        }
        return res;
    }
}
