package nl.esciencecenter.praline.data;

public class Alphabet {
    private String name;
    private int length;

    public Alphabet(String name) {
        this.name = name;
        length = 0;
    }

    public Alphabet(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        length = length;
    }
}
