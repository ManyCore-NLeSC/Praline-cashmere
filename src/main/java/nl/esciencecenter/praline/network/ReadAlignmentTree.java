package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.AlignmentTree;

import java.util.Vector;

public class ReadAlignmentTree {

    AlignmentTree readTree(int nrLeafs, String s){
        Vector<AlignmentTree> treeArr = new Vector<>();
        for(int i = 0 ; i < nrLeafs; i++){
            treeArr.add(new AlignmentTree(i));
        }
        String[] ss = s.split(" ");
        AlignmentTree res = null;
        for(String v : ss){
            String[] mv = v.split(",");
            int merge = Integer.parseInt(mv[0]);
            int with = Integer.parseInt(mv[1]);
            res = new AlignmentTree(treeArr.get(merge), treeArr.get(with));
            treeArr.set(merge,res);
            treeArr.remove(with);
        }
        return res;
    }
}
