package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public class Sequence {
    private String name;
    private int length;
    private ArrayList<Track> tracks;

    public Sequence(String name) {
        this(name,0);
    }

    public Sequence(String name, int length) {
        this.name = name;
        this.length = length;
        this.tracks = new ArrayList<>();
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

    public int getElement(int index){
        return getElement(0,index);
    }

    public int getElement(int trackNr, int index) {
        if (trackNr >= 0 && trackNr < tracks.size()
                && (index >= 0) && (index < tracks.get(trackNr).getLength())) {
            return tracks.get(trackNr).getValue(index);
        }
        throw new Error("Out of bounds!" + index);
    }


    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public Track getTrack(int i){
        if(i >= 0 && i < tracks.size()){
            return tracks.get(i);
        }
        throw new Error("No such track!");
    }

    public void addTrack(Track track) {
        if ( tracks.size() == 0 || track.getLength() == length ) {
            tracks.add(track);
            length = track.getLength();
        } else {
            throw new Error("Track not of correct length!");
        }
    }

    public int getNrTracks() {
        return tracks.size();
    }
}
