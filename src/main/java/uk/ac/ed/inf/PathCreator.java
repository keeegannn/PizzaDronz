package uk.ac.ed.inf;

import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.Set;

public class PathCreator {
    public void createPath(LngLat appleton, LngLat restaurant, Set<NamedRegion> no_fly_zones, NamedRegion centralArea, PathFinder pathFinder, LngLatHandler handler){
        pathFinder.findPath(appleton, restaurant, no_fly_zones, handler, false, centralArea);
        LngLat destination = handler.findCentreOfRectangle(centralArea);
        pathFinder.findPath(restaurant, destination, no_fly_zones, handler, true, centralArea);
        int pathlength = pathFinder.getPath().size();
        LngLat newstart = pathFinder.getPath().get(pathlength - 1);
        pathFinder.findPath(newstart, appleton, no_fly_zones, handler, false, centralArea);
    }
}
