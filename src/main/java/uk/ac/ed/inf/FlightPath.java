package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class FlightPath {
    private LngLat[] vertices;
    public LngLat[] getVertices(){
        return this.vertices;
    }
    public void setVertices(LngLat[] new_vertices){
        this.vertices = new_vertices;
    }
}
