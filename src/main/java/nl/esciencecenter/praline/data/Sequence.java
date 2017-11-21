package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class Sequence {
    private String name;
    private int length;
    private ArrayList<Track> tracks;

    public Sequence(String name) {
        this.name = name;
    }

    public Sequence(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public Sequence(String name, int length, ArrayList<Track> tracks) {
        this.name = name;
        this.length = length;
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        if ( track.getLength() == length ) {
            tracks.add(track);
        } else {
            // TODO: throw exception
        }
    }

    public int getNrTracks() {
        return tracks.size();
    }
}
