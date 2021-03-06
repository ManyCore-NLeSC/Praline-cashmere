package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignResult;
import nl.esciencecenter.praline.data.Matrix2DF;
import nl.esciencecenter.praline.data.AlignStep;
import nl.esciencecenter.praline.data.AlignmentMode;
import nl.esciencecenter.praline.gapcost.AffineGapCost;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestAlign {

    static final int TEST_ALPHABET_SIZE = 3;

    public static float getAlignScore(List<AlignStep> align, int sizeA, int sizeB, IGapCost gapCostA, IGapCost gapCostB, IPositionCost costs) {
        float score = 0;
        int indexA = 0;
        int indexB = 0;
        int curGapA = 0;
        int curGapB = 0;
        for(AlignStep step : align) {
            switch (step) {
                case ALIGN:
                    if(curGapA > 0){
                        score+= gapCostA.getGapCost(curGapA);
                        curGapA = 0;
                    }
                    if(curGapB > 0){
                        score+= gapCostB.getGapCost(curGapB);
                        curGapB = 0;
                    }
                    score += costs.cost(indexA,indexB); indexA++; indexB++; break;
                case GAPA:
                    if(curGapB > 0){
                        score+= gapCostB.getGapCost(curGapB);
                        curGapB = 0;
                    } curGapA++; indexB++; break;
                case GAPB:
                    if(curGapA > 0){
                        score+= gapCostA.getGapCost(curGapA);
                        curGapA = 0;
                    }
                    curGapB++; indexA++; break;
            }
        }
        if(curGapA > 0){
            score+= gapCostA.getGapCost(curGapA);
        }
        if(curGapB > 0){
            score+= gapCostB.getGapCost(curGapB);
        }
        return score;
    }

    static int[] randomString(int length){
        Random r = new Random();
        int[] res = new int[length];
        for(int i = 0 ; i < length ; i++){
            res[i] = r.nextInt(TEST_ALPHABET_SIZE);
        }
        return res;
    }
//
//    static void test(){
//        int[] a = new int[]{2};
//        int[] b = new int[]{2,2,2,0};
//
//        AlignResult res = new AffineGapAligner().align(a.length,b.length,new AffineGapCost(-2, -1),
//                new AffineGapCost(-3,-2), testPositionCost(a,b), AlignmentMode.GLOBAL);
//        System.out.println();
//        System.out.println();
//        new ReferenceAligner().align(a.length,b.length,new AffineGapCost(-2, -1),
//                new AffineGapCost(-3,-2), testPositionCost(a,b), AlignmentMode.GLOBAL);
//
////        for(AlignStep x : res.align..steps){
////            System.out.println(x.name());
////        }
////        printAlign("2","2220", res.align);
////        System.out.printf("%d %d \n",res.score, getAlignScore(res.align,a.length,b.length,new AffineGapCost(-2, -1),
////                new AffineGapCost(-3,-2), testPositionCost(a,b) ));
//
//    }

    static final float MAX_DIFF = 0.5f;

    static boolean testAlign(IAlign alignOracle, IAlign align, IGapCost gapCostA, IGapCost gapCostB, int lenA, int lenB, boolean measure){
        int[] stringA = randomString(lenA);
        int[] stringB = randomString(lenB);
        try {
            long start = System.currentTimeMillis();
            AlignResult oracleRes = alignOracle.align(stringA.length,stringB.length,gapCostA,gapCostB, testPositionCost(stringA,stringB), AlignmentMode.GLOBAL);
            System.out.println("Oracle done");
            long end = System.currentTimeMillis();
            long durOracle = end - start;
            start = System.currentTimeMillis();
            AlignResult res = align.align(stringA.length,stringB.length,gapCostA,gapCostB,testPositionCost(stringA,stringB), AlignmentMode.GLOBAL);
            System.out.println("custom done");
            end = System.currentTimeMillis();
            long durAlign = end - start;
            float score = getAlignScore(res.getAlignSteps(),stringA.length,stringB.length,gapCostA,gapCostB, testPositionCost(stringA,stringB));
            if(Math.abs(res.getScore() - oracleRes.getScore()) > MAX_DIFF){
                System.out.printf("Wrong score reported: %f actual %f \n A: \n", res.getScore(), oracleRes.getScore());
                System.out.println(Arrays.toString(stringA));
                System.out.printf(" \n B: \n");
                System.out.println(Arrays.toString(stringB));
                return false;
            }
            if (Math.abs(res.getScore() - score) > MAX_DIFF) {
                System.out.printf("Wrong answer reported: %f actual %f (size) %d \n A: \n", res.getScore(), score, res.getAlignSteps().size());
                System.out.println(Arrays.toString(stringA));
                System.out.printf(" \n B: \n");
                System.out.println(Arrays.toString(stringB));
                return false;
            }

            if(measure){
                System.out.printf("reference took %5d ms, align took %5d\n", durOracle,durAlign);
            }
        } catch (Exception e){
            System.out.printf("Wrong answer on: \n A: \n");
            System.out.println(Arrays.toString(stringA));
            System.out.printf(" \n B: \n");
            System.out.println(Arrays.toString(stringB));
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static void testAligner(IAlign alignOracle, IAlign align,  IGapCost gapCostA, IGapCost gapCostB, int minLength, int maxLength, int nrTries){
        Random r = new Random();
        for(int i = 0 ; i < nrTries ; i++) {
            int diff = maxLength - minLength;
            int lenA = minLength + r.nextInt(diff + 1);
            int lenB = minLength + r.nextInt(diff + 1);
            if(!testAlign(alignOracle,align,gapCostA,gapCostB,lenA,lenB,true)){
                return;
            }

        }
    }


    static final Matrix2DF testScoreMatrix = new Matrix2DF(new float[][] {
            new float[] {10f, 0f, -2f },
            new float[] {0 , 6f, -4f},
            new float[] {-2, -4, 2} });

    static IPositionCost testPositionCost(int[] a, int[] b){
        return makeCost(testScoreMatrix,a,b);
    }

    static IPositionCost makeCost(Matrix2DF scoreMatrix, int[] a, int[] b){
        return new IPositionCost(){

            @Override
            public float cost(int posA, int posB) {
                return scoreMatrix.get(a[posA], b[posB]);
            }
        };

    }

    static IPositionCost makeCost(String a, String b){
        return new IPositionCost(){

            @Override
            public float cost(int posA, int posB) {
                return a.charAt(posA) == b.charAt(posB) ? 1 : -1;
            }
        };

    }
//
//    static AlignResult stringAlign(String a, String b){
//        IAlign align = new NonWorkingLinearGapLinearSpaceAligner(5,2);
//        AlignResult r = align.align(a.length(),b.length(), new LinearGapCost(-1), new LinearGapCost(-1), makeCost(a,b));
//        AlignResult refRes = new ReferenceAligner().align(a.length(),b.length(), new LinearGapCost(-1), new LinearGapCost(-1), makeCost(a,b));
//        float checkedScore = getAlignScore(r.align, a.length(),b.length(), new LinearGapCost(-1), new LinearGapCost(-1), makeCost(a,b) );
//
//        boolean good = checkedScore == r.score;
//        if (good) {
//            System.out.println("Good!");
//        } else {
//            System.out.printf("Not good, result score %f checked %f", r.score, checkedScore);
//        }
//        boolean good2 = refRes.score == r.score;
//        if (good2) {
//            System.out.println("Good!");
//        } else {
//            System.out.printf("Not good, result score %f reference %f", r.score, refRes.score );
//        }
//        return r;
//    }

    public static void main(String[] argv){
        //testAligner(new ReferenceAligner(), new ReferenceAligner(), new LinearGapCost(-1), new LinearGapCost(-1.1f), 4,100, 10000);
        // IAlign aling = new NonWorkingLinearGapLinearSpaceAligner(100,100);
       testAligner( new AffineGapAligner(),new ReferenceAligner(), new AffineGapCost(-4f, -2f),
               new AffineGapCost(-4f,-2f), 500,1000, 1000000);
       // test();
        //testAligner(new LinearGapAligner(), new AffineGapAligner(), new LinearGapCost(-1), new LinearGapCost(-2),3, 5000,30000);
//        testAlign(new LinearGapAligner(), new AffineGapAligner(), new LinearGapCost(-1), new LinearGapCost(-2), 10000,10000,true );
//        System.out.println("DONE!");
//
//        String a = "GCATGCU";
//        String b = "GATTACA";
//        AlignResult res = stringAlign(a,b);
//
//        System.out.println(res.score);
//        //System.out.println(res.align.size());
//        printAlign(a,b, res.align);

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
