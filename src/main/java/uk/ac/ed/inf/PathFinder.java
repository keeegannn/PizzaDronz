package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder {
    private ArrayList<LngLat> path;
    private List<Double> angles;

    public PathFinder() {
        path = new ArrayList<>();
        angles = new ArrayList<>();
    }
    public void findPath(LngLat start, LngLat goal, Set<NamedRegion> obstacles, LngLatHandler handler, boolean IsToCentral, NamedRegion centralArea) {
        // Create sets for open and closed nodes
        Set<LngLat> closedSet = new HashSet<>();

        //creat priority queue for open nodes
        ArrayList<LngLat> openSet = new ArrayList<>();


        // Create a map to store the cost of reaching each node from the start node
        HashMap<LngLat, Double> gScore = new HashMap<>();

        //Create a map  to store heuristic Cost
        HashMap<LngLat, Double> hScore = new HashMap<>();

        // Create a map to store the estimated total cost of reaching each node (gScore + heuristic)
        HashMap<LngLat, Double> fScore = new HashMap<>();


        LngLat current = start;
        // Initialize the start node
        gScore.put(start, 0.0);
        hScore.put(start, handler.distanceTo(start, goal));
        fScore.put(start, hScore.get(start) + gScore.get(start));
        path.add(start);

        while ((!handler.isCloseTo(current, goal)) || (IsToCentral && !handler.isInCentralArea(current, centralArea))) {
            openSet.remove(current);
            closedSet.add(current);
            findNeighbors(current, closedSet, handler, obstacles, openSet);
            findGscore(start, openSet, handler, gScore);
            findHscore(goal, openSet, handler, hScore);
            findFscore(openSet, gScore, hScore, fScore);
           // System.out.println("current : " + current);
            current = getOptimalLngLat(current, openSet, hScore, fScore);

        }
        if(!IsToCentral) {
            angles.add(999.0);
        }
    }
    public void findNeighbors (LngLat current, Set<LngLat> closed, LngLatHandler handler, Set<NamedRegion> no_fly_zones, ArrayList<LngLat> openset){
        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
        for(double angle : angles){
            LngLat position = handler.nextPosition(current, angle);
            for(NamedRegion region : no_fly_zones){
                if(!handler.isInRegion(position, region)  && !closed.contains(position) && !openset.contains(position)){
                    openset.add(position);
                }
            }
        }
        openset.remove(current);
        closed.add(current);
    }
    public void findHscore(LngLat goal, ArrayList<LngLat> openSet, LngLatHandler handler, HashMap<LngLat, Double> Hscores){
        for(LngLat point : openSet){
            double hScore = handler.distanceTo(point, goal);
            Hscores.put(point, hScore);
        }
    }
    public void findGscore(LngLat start, ArrayList<LngLat> openSet, LngLatHandler handler, HashMap<LngLat, Double> Gscores) {
        for (LngLat point : openSet) {
            double gScore = handler.distanceTo(start, point);
            // Accumulate the gScore for each neighbor
            Gscores.put(point, gScore);
        }
    }
    public void findFscore(ArrayList<LngLat> openSet, HashMap<LngLat, Double> Gscores, HashMap<LngLat, Double> Hscores, HashMap<LngLat, Double> Fscores) {
        for (LngLat point : openSet) {
            double fScore = Gscores.get(point) + Hscores.get(point);
            // Accumulate the gScore for each neighbor
            Fscores.put(point, fScore);
        }
    }

    public LngLat getOptimalLngLat(LngLat current, ArrayList<LngLat> openset, HashMap<LngLat, Double> hScores, HashMap<LngLat, Double> fScores) {
        LngLat optimalLngLat = openset.get(0);
        HashSet<LngLat> notChosen = new HashSet<>();
        for (LngLat position : openset) {
            if (fScores.get(position) < fScores.get(optimalLngLat) ||
                    (fScores.get(position).equals(fScores.get(optimalLngLat)) && hScores.get(position) < hScores.get(optimalLngLat))) {
                optimalLngLat = position;
            }
            else{
                notChosen.add(position);
            }
        }
        for(LngLat unwanted : notChosen){
            openset.remove(unwanted);
        }
        boolean x = fScores.get(current) >= fScores.get(optimalLngLat);
        openset.remove(optimalLngLat);
        path.add(optimalLngLat);
        angles.add(calculateAngle(current, optimalLngLat));
        return optimalLngLat;
    }
    private double calculateAngle(LngLat from, LngLat to) {
        double angle = Math.toDegrees(Math.atan2(to.lat() - from.lat(), to.lng() - from.lng()));
        double roundedAngle = Math.round(angle * 10.0) / 10.0; // Round to one decimal point
        return roundedAngle >= 0 ? roundedAngle : 360 + roundedAngle;
    }


    public List<LngLat> getPath() {
        return path;
    }

    public List<Double> getAngles() {
        return angles;
    }
    public JsonObject ToJson(){
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "LineString");
        JsonArray coordinates = new JsonArray();
        for(LngLat point : path){
            JsonArray pointArray = new JsonArray();
            pointArray.add(point.lng());
            pointArray.add(point.lat());
            coordinates.add(pointArray);
        }
        geoJson.add("coordinates", coordinates);
        return geoJson;
    }
}
