package nl.esciencecenter.praline.aligners;

public class MCLAligner {

    static final int GAP_A = 1;
    static final int GAP_B = 2;
    static final int GAP_AB = 3;
    static final int ALIGN = 4;

    float gapCost(float costGapStart, float costGapExtend, int length) {
	if (length == 0) {
	    return 0.0f;
	}
	else {
	    return costGapStart + costGapExtend * (length - 1);
	}
    }


    // costMatrix is the costMatrix

    // global??
    // neem aan lengthA >= lengthB
    void align(int lengthA, int lengthB, int sizeAlphabetMax,
	    int nrTracks,
	    int[/*nrTracks*/] sizesAlphabet,
	    float[/*nrTracks*/][/*sizeAlphabetMax*/][/*lengthA*/] a,
	    float[/*nrTracks*/][/*sizeAlphabetMax*/][/*lengthA*/] b,
	    float[/*lengthB, lengthA*/][] costMatrix,
	    int[/*lengthB, lengthA*/][] traceBack,

	    // called alignCosts in MotifPositionCost
	    float[/*nrTracks*/][/*sizeAlphabetMax, sizeAlphabetMax*/][] cost,
	    float costGapStart,
	    float costGapExtend) {
	//Coordinate[lengthA+lengthB] path,
	//float score
	
	for (int t = 0; t < lengthA; t++) {
	    for (int i = 0 ; i < lengthA + lengthB - 1; i++) {

		int y = i-t;
		int x = t;
	    
		if(y < 0 || y >= lengthA){
		    // idle
		} else if (x == 0 && y > 0) {
		    costMatrix[y][0] = gapCost(costGapStart, costGapExtend, y);
		    traceBack[y][0] = GAP_B;
		}
		else if (y == 0 && x > 0) {
		    costMatrix[0][x] = gapCost(costGapStart, costGapExtend, x);
		    traceBack[0][x] = GAP_A;
		} else if (x == 0 && y == 0) {
		    costMatrix[0][0] = 0;
		    traceBack[0][0] = 0;
		}
		else {
		    float gapB = costMatrix[y-1][x];
		    if((traceBack[y-1][x] & GAP_B) == GAP_B){
			gapB+=costGapExtend;
		    } else {
			gapB+=costGapStart;
		    }
		    float gapA = costMatrix[y][x-1];
		    if((traceBack[y][x-1] & GAP_A) == GAP_A){
			gapA+=costGapExtend;
		    } else {
			gapA+=costGapStart;
		    }
		    float align = costMatrix[y-1][x+1];

		    for(int track = 0 ; track <nrTracks; track++){
			for(int ia = 0 ; ia < sizesAlphabet[track] ; ia++){
			    for(int ib = 0 ; ib < sizesAlphabet[track] ; ib++){
				align += a[track][ia][y] * b[track][ib][x] * cost[track][ia][ib];
			    }
			}
		    }
		    if(gapA < align){
			if(gapB < gapA){
			    costMatrix[y][x] = gapB;
			    traceBack[y][x] = GAP_B;
			} else if (gapB == gapA){
			    costMatrix[y][x] = gapB;
			    traceBack[y][x] = GAP_AB;
			} else {
			    costMatrix[y][x] = gapA;
			    traceBack[y][x] = GAP_A;
			}
		    } else if (gapB < align) {
			costMatrix[y][x] = gapB;
			traceBack[y][x] = GAP_A;
		    } else {
			costMatrix[y][x] = align;
			traceBack[y][x] = ALIGN;
		    }
		}
	    }
	}
    }
}
