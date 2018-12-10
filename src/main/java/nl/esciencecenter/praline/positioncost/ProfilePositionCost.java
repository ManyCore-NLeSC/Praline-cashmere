package nl.esciencecenter.praline.positioncost;

import nl.esciencecenter.praline.data.Matrix2DF;

public class ProfilePositionCost implements IPositionCost {

    // first dimension : position
    // second dimension: symbol
    final Matrix2DF profileA;
    final Matrix2DF profileB;
    final Matrix2DF costMatrix;

    public ProfilePositionCost(Matrix2DF profileA, Matrix2DF profileB, Matrix2DF costMatrix) {
        this.profileA = profileA;
        this.profileB = profileB;
        this.costMatrix = costMatrix;
    }
/*
    MatrixF outerProduct(VectorF a, VectorF b){
        assert a.size() == b.size();

        MatrixF res = new Matrix2DF(a.size(), b.size() );
        for(int i = 0 ; i < a.size(); i++){
            for(int j = 0 ; j < a.size() ; j++){
                res.set(i,j,a.get(i) * b.get(j));
            }
        }
        return res;
    }

    MatrixF pieceWiseProduct(MatrixF a, MatrixF b){
        assert a.nrRows() == b.nrRows() && a.nrCols() == b.nrCols();
        MatrixF res = new Matrix2DF(a.nrRows(),a.nrCols());
        for(int i = 0 ; i < a.nrRows() ; i++){
            for(int j = 0 ; j < a.nrCols() ; j++){
                res.set(i,j, a.get(i,j) * b.get(i,j));
            }
        }
        return res;
    }

    float sumMatrix(MatrixF a){
        float res = 0;
        for(int i = 0; i < a.nrRows(); i++){
            for(int j = 0 ; j < a.nrCols() ; j++){
                res += a.get(i,j);
            }
        }
        return res;
    }
*/

    @Override
    public float cost(int posA, int posB) {

        // does this
        /*
        Matrix2DF m = outerProduct(profileA,indexa, profileB, indexb);
        Matrix2DF c = pieceWiseProduct(m, costMatrix);
        return sumMatrix(c);
        */
        /* or this

         */
        /*
        float res = 0 ;
        for(int i = 0 ; i < profileA.nrCols ; i++){
            for(int j = 0 ; j <= i ; j++){
                res += costMatrix.get(i,j) * profileA.get(indexa,i) * profileB.get(indexb,j);
                res += costMatrix.get(i,j) * profileA.get(indexa,j) * profileB.get(indexb,i);
            }
        }
        return res;
        */

        float res = 0 ;
        for(int i = 0 ; i < profileA.nrCols() ; i++){
            for(int j = 0 ; j < profileA.nrCols() ; j++){
                res += costMatrix.get(i,j) * profileA.get(posA,i) * profileB.get(posB,j);
            }
        }
        return res;
    }
}
