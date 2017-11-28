package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.data.Move;
import nl.esciencecenter.praline.data.ScoreResult;

import java.util.ArrayList;
import java.util.List;

public class CostAligner {

    ScoreResult[][] align(boolean local, int seqLengthA, int seqLengthB, float[][] cost, float gapCost){
        ScoreResult[][] matrix = new ScoreResult[seqLengthA + 1][];
        for(int i = 0 ; i < seqLengthA ; i++){
            matrix[i] = new ScoreResult[seqLengthB + 1];
        }
        for(int i = 0 ; i < seqLengthA + 1 ; i++){
            matrix[i][0] = local ?
                    new ScoreResult(0,Move.NIL) :
                    new ScoreResult(i * gapCost, Move.LEFT);

        }
        for(int j = 0 ; j < seqLengthB + 1; j++){
            matrix[0][j] = local ?
                    new ScoreResult(0,Move.NIL) :
                    new ScoreResult(j * gapCost, Move.TOP);
        }
        for(int i = 1 ; i < seqLengthA + 1; i++){
            for(int j = 1 ; j < seqLengthB + 1; j++){

                float match  = matrix[i-1][j-1].score +  cost[i][j];
                float delete = matrix[i-1][j].score + gapCost;
                float insert = matrix[i][j-1].score + gapCost;
                ScoreResult best = local ?
                        new ScoreResult(Float.MIN_VALUE, Move.NIL) :
                        new ScoreResult(0,Move.NIL);

                if(match > best.score ) best = new ScoreResult(match,Move.TOP_LEFT);
                if(delete > best.score) best = new ScoreResult(delete, Move.LEFT);
                if(insert > best.score) best = new ScoreResult(insert, Move.TOP);

                matrix[i][j] = best;
            }
        }
        return matrix;
    }

    Coordinate maxIndex(ScoreResult[][] mat){
        float best = mat[0][0].score;
        int x = 0;
        int y = 0;
        for(int i = 0 ; i < mat.length ; i++){
            for(int j = 0 ; j < mat[0].length ; j++){
                if(mat[i][j].score > best){
                    best = mat[i][j].score;
                    x = i ; y = j;
                }
            }
        }
        return new Coordinate(x,y);
    }

}
