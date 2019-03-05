// fermi
#define ALIGN (4)
#define GAP_AB (3)
#define GAP_A (1)
#define GAP_B (2)

float gapCost(const float costGapStart, const float costGapExtend, const int length);




__kernel void alignKernel(const int lengthA, const int lengthB, const int sizeAlphabetMax, const int nrTracks, const __global int* sizesAlphabet, const __global float* a, const __global float* b, __global float* dynMatrix, __global int* traceBack, const __global float* cost, const float costGapStart, const float costGapExtend) {
    const int bt = get_group_id(0);
    const int wtt = get_local_id(1);
    const int ttt = get_local_id(0);

    const int nrThreadsLengthA = min(1024, lengthA);
    const int nrBlocksLengthA = lengthA == 1 * nrThreadsLengthA ?
        1 :
        lengthA % (1 * nrThreadsLengthA) == 0 ?
            lengthA / (1 * nrThreadsLengthA) :
            lengthA / (1 * nrThreadsLengthA) + 1
    ;
    const int nrThreadsNrThreadsLengthA = min(32, nrThreadsLengthA);
    const int nrWarpsNrThreadsLengthA = nrThreadsLengthA == 1 * nrThreadsNrThreadsLengthA ?
        1 :
        nrThreadsLengthA % (1 * nrThreadsNrThreadsLengthA) == 0 ?
            nrThreadsLengthA / (1 * nrThreadsNrThreadsLengthA) :
            nrThreadsLengthA / (1 * nrThreadsNrThreadsLengthA) + 1
    ;
    const int tt = wtt * (1 * nrThreadsNrThreadsLengthA) + ttt;
    if (tt < nrThreadsLengthA) {
        const int t = bt * (1 * nrThreadsLengthA) + tt;
        if (t < lengthA) {
            for (int i = 0; i < lengthA + lengthB - 1; i++) {
                const int y = i - t;
                const int x = t;
                if (y < 0 || y >= lengthA) {
                    
                }
                else if (x == 0 && y > 0) {
                    dynMatrix[0 + y * (1 * lengthA)] = gapCost(costGapStart, costGapExtend, y);
                    traceBack[0 + y * (1 * lengthA)] = GAP_B;
                }
                else if (y == 0 && x > 0) {
                    dynMatrix[x + 0 * (1 * lengthA)] = gapCost(costGapStart, costGapExtend, x);
                    traceBack[x + 0 * (1 * lengthA)] = GAP_A;
                }
                else if (x == 0 && y == 0) {
                    dynMatrix[0 + 0 * (1 * lengthA)] = 0;
                    traceBack[0 + 0 * (1 * lengthA)] = 0;
                }
                else {
                    float gapB = dynMatrix[x + (y - 1) * (1 * lengthA)];
                    if ((traceBack[x + (y - 1) * (1 * lengthA)] & GAP_B) == GAP_B) {
                        gapB += costGapExtend;
                    }
                    else {
                        gapB += costGapStart;
                    }
                    float gapA = dynMatrix[x - 1 + y * (1 * lengthA)];
                    if ((traceBack[x - 1 + y * (1 * lengthA)] & GAP_A) == GAP_A) {
                        gapA += costGapExtend;
                    }
                    else {
                        gapA += costGapStart;
                    }
                    float align = dynMatrix[x + 1 + (y - 1) * (1 * lengthA)];
                    for (int track = 0; track < nrTracks; track++) {
                        for (int ia = 0; ia < sizesAlphabet[track]; ia++) {
                            for (int ib = 0; ib < sizesAlphabet[track]; ib++) {
                                align += a[y + track * (1 * sizeAlphabetMax * lengthA) + ia * (1 * lengthA)] * b[x + track * (1 * sizeAlphabetMax * lengthB) + ib * (1 * lengthB)] * cost[ia + track * (1 * sizeAlphabetMax)];
                            }
                        }
                    }
                    if (gapA < align) {
                        if (gapB < gapA) {
                            dynMatrix[x + y * (1 * lengthA)] = gapB;
                            traceBack[x + y * (1 * lengthA)] = GAP_B;
                        }
                        else if (gapB == gapA) {
                            dynMatrix[x + y * (1 * lengthA)] = gapB;
                            traceBack[x + y * (1 * lengthA)] = GAP_AB;
                        }
                        else {
                            dynMatrix[x + y * (1 * lengthA)] = gapA;
                            traceBack[x + y * (1 * lengthA)] = GAP_A;
                        }
                    }
                    else if (gapB < align) {
                        dynMatrix[x + y * (1 * lengthA)] = gapB;
                        traceBack[x + y * (1 * lengthA)] = GAP_A;
                    }
                    else {
                        dynMatrix[x + y * (1 * lengthA)] = align;
                        traceBack[x + y * (1 * lengthA)] = ALIGN;
                    }
                }
            }
        }
    }
}

float gapCost(const float costGapStart, const float costGapExtend, const int length) {
    

    if (length == 0) {
        return 0.0;
    }
    else {
        return costGapStart + costGapExtend * (length - 1);
    }
}




