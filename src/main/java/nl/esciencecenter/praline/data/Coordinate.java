package nl.esciencecenter.praline.data;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return this.x + ";" + this.y;
    }
}
