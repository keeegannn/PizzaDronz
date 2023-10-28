package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

public class PathFinder {
    private static LngLat startposition;
    private static LngLat endposition;
    public PathFinder(LngLat startpos){
        startposition = startpos;
    }
    public FlightPath findpath(Order order, Restaurant restaurant, NamedRegion[] no_fly_zones, NamedRegion central_area){
        return null;
    }

    public static void setStartposition(LngLat startposition) {
        PathFinder.startposition = startposition;
    }

    public static LngLat getStartposition() {
        return startposition;
    }

    public static LngLat getEndposition() {
        return endposition;
    }
    public void nextpath(){
        startposition = endposition;
    }
}
