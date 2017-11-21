package nl.esciencecenter.praline.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class SequenceTest {
    private final String name = "Test";
    private final int length = 11;
    private final int [] elements = {0, 1, 2, 6, 72, 1, 0, 12, 7, 7, 9};

    @Test
    public void getId() {
        Sequence sequence = new Sequence(name);

        assertEquals(name, sequence.getName());
        assertNotEquals("", sequence.getName());
    }

    @Test
    public void getLength() {
        Sequence sequence = new Sequence(name, length);

        assertEquals(length, sequence.getLength());
        assertNotEquals(0, sequence.getLength());
    }

    @Test
    public void getElement() {
        Sequence sequence = new Sequence(name, elements);

        assertEquals(0, sequence.getElement(0));
        assertEquals(0, sequence.getElement(6));
        assertEquals(9, sequence.getElement(10));
        assertEquals(-1, sequence.getElement(12));
    }

    @Test
    public void setElement() {
        Sequence sequence = new Sequence(name, length);

        sequence.setElement(0, 12);
        assertEquals(12, sequence.getElement(0));
        sequence.setElement(length - 1, 42);
        assertEquals(42, sequence.getElement(length - 1));
    }

    @Test
    public void getElements() {
        int iterator = 0;
        Sequence sequence = new Sequence(name, elements);

        for ( int element : sequence.getElements() ) {
            assertEquals(elements[iterator], element);
            iterator++;
        }
    }

    @Test
    public void setElements() {
        int iterator = 0;
        Sequence sequence = new Sequence(name);

        sequence.setElements(elements);
        for ( int element : sequence.getElements() ) {
            assertEquals(elements[iterator],element);
            iterator++;
        }
    }
}