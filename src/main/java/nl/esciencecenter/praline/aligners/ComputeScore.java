package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.AlignStep;
import nl.esciencecenter.praline.gapcost.IGapCost;
import nl.esciencecenter.praline.positioncost.IPositionCost;

import java.util.List;

public class ComputeScore {

    // compute align score from align steps and gap and position costs
    // this is used as a sanity check, the score reported here
    // and the score computed by an alignment algorithm should
    // be the same
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
}
