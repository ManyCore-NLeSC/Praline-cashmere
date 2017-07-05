package nl.esciencecenter.praline.containers;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlphabetTest {
    private final String nameOne = "TestOne";
    private final int lengthOne = 65;
    private final int lengthTwo = 65;

    @Test
    public void getName() {
        Alphabet alphabet = new Alphabet(nameOne);

        assertEquals(nameOne, alphabet.getName());
        assertNotEquals("", alphabet.getName());
    }

    @Test
    public void getLength() {
        Alphabet alphabet = new Alphabet(nameOne, lengthOne);

        assertEquals(lengthOne, alphabet.getLength());
        assertNotEquals(lengthOne - 1, alphabet.getLength());
        alphabet.setLength(lengthTwo);
        assertEquals(lengthTwo, alphabet.getLength());
    }
}
