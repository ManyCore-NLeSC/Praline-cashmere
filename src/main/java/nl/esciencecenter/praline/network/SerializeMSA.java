package nl.esciencecenter.praline.network;

import nl.esciencecenter.praline.data.AlignmentTree;
import nl.esciencecenter.praline.data.MSATree;
import nl.esciencecenter.praline.data.Matrix2DI;

import java.util.Vector;

public class SerializeMSA {



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

    public static String serializeMSA(MSATree res){
        StringBuilder b = new StringBuilder();
        for(int row = 0 ; row < res.coordinates.nrRows; row++){
            for(int col = 0; col < res.coordinates.nrCols; col++){
                b.append(res.coordinates.get(row,col));
                if(col != res.coordinates.nrCols -1) {
                    b.append(';');
                }
            }
            b.append(' ');
        }
        return b.toString();
    }
}
