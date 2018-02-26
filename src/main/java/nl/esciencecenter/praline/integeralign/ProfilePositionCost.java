package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Matrix;
import nl.esciencecenter.praline.data.Matrix2DF;

public class ProfilePositionCost implements IPositionCost{

    final Matrix2DF profileA;
    final Matrix2DF profileB;
    final Matrix2DF costMatrix;

    public ProfilePositionCost(Matrix2DF profileA, Matrix2DF profileB, Matrix2DF costMatrix) {
        this.profileA = profileA;
        this.profileB = profileB;
        this.costMatrix = costMatrix;
    }

    Matrix2DF outerProduct(Matrix2DF a, int indexa, Matrix2DF b, int indexb){
        Matrix2DF res = new Matrix2DF(a.nrCols, a.nrCols );
        assert b.nrCols == a.nrCols;
        for(int i = 0 ; i < a.nrCols ; i++){
            for(int j = 0 ; j < a.nrCols ; j++){
                res.set(i,j,a.get(indexa,i) * b.get(indexb,j));
            }
        }
        return res;
    }

    Matrix2DF pieceWiseProduct(Matrix2DF a, Matrix2DF b){
        assert a.nrRows == b.nrRows && a.nrCols == b.nrCols;
        Matrix2DF res = new Matrix2DF(a.nrRows,a.nrCols);
        for(int i = 0 ; i < a.nrRows ; i++){
            for(int j = 0 ; j < a.nrCols ; j++){
                res.set(i,j, a.get(i,j) * b.get(i,j));
            }
        }
        return res;
    }

    float sumMatrix(Matrix2DF a){
        float res = 0;
        for(int i = 0; i < a.nrRows; i++){
            for(int j = 0 ; j < a.nrCols ; j++){
                res += a.get(i,j);
            }
        }
        return res;
    }

    @Override
    public float cost(int posA, int posB) {
        Matrix2DF outer = outerProduct(profileA,posA, profileB, posB);
        Matrix2DF pw = pieceWiseProduct(outer, costMatrix);
        return sumMatrix(pw) ;
    }
}
