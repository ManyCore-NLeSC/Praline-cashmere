package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.PositionCost;

import java.util.ArrayList;
import java.util.List;

public class TestAlign {


    static IPositionCost makeCost(String a, String b){
        return new IPositionCost(){

            @Override
            public float cost(int posA, int posB) {
                return a.charAt(posA) == b.charAt(posB) ? 1 : -1;
            }
        };

    }

    static AlignResult stringAlign(String a, String b){
        IAlign align = new ReferenceO3Aligner();
        return align.align(a.length(),b.length(), new LinearGapCost(-1), new LinearGapCost(-1), makeCost(a,b));
    }

    public static void main(String[] argv){
        String a = "GCATGCU";
        String b = "GATTACA";
        AlignResult res = stringAlign(a,b);
        System.out.println(res.score);
        //System.out.println(res.align.size());
        printAlign(a,b, res.align);
    }

    static void printAlign(String a, String b, List<AlignStep> res){
        String ar= "", br = "";
        int i = 0, j = 0;
        for(AlignStep s : res){
            switch (s){
                case ALIGN: ar += a.charAt(i); br+= b.charAt(j); i++; j++; break;
                case GAPA : ar += "-" ; br+= b.charAt(j); j++; break;
                case GAPB : ar += a.charAt(i); br+= "-" ; i++; break;
            }
        }
        if(i < a.length()){
            ar += a.substring(i);
        }
        if(j < b.length()){
            br += b.substring(j);
        }
        System.out.println(ar);
        System.out.println(br);

    }
}
