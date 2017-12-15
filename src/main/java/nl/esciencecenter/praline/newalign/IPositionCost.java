package nl.esciencecenter.praline.aligners;

public interface IPositionCost {
    float cost(int posA, int posB);
}
