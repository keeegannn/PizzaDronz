package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder {

    public ArrayList<LngLat> findPath(LngLat start, LngLat goal, Set<NamedRegion> obstacles, LngLatHandler handler, boolean IsToCentral, NamedRegion centralArea) {


        // Create sets for open and closed nodes
        Set<LngLat> closedSet = new HashSet<>();

        //creat priority queue for open nodes
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(node -> node.getfCost()));

        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

        // Initialize the start node
        Node current = new Node(start, null, handler.distanceTo(start, goal), 0, handler.distanceTo(start, goal));

        openSet.add(current);

        while (!openSet.isEmpty()) {

            current = openSet.poll();

            if (handler.isCloseTo(current.getPosition(), goal)) {
                break;
            }
            for (double angle : angles) {
                //find all possible next positions
                LngLat position = handler.nextPosition(current.getPosition(), angle);
                if (closedSet.contains(position)) {
                    continue;
                }
                if (inNoFlyZones(position, handler, obstacles, centralArea)) {
                    closedSet.add(position);
                    continue;
                }//find Scores for each position not in the closed set
                double Gscore = current.getgCost() + SystemConstants.DRONE_MOVE_DISTANCE * 0.55;
                double Hscore = handler.distanceTo(position, goal);
                double Fscore = Gscore + Hscore;
                Node positionNode = new Node(position, current, Fscore, Gscore, Hscore);
                openSet.add(positionNode);
                //update processed point in open and closed set
                closedSet.add(current.getPosition());
            }
        }
        return reconstructPath(current, IsToCentral);
    }
    public ArrayList<LngLat> reconstructPath(Node goal, boolean IsToCentral) {
        ArrayList<LngLat> path = new ArrayList<>();
        //get last point of the path
        Node current = goal;
        //retrace the path saving each point
            while (current != null) {
                path.add(current.getPosition());
                current = current.getParent();
            }
        // Reverse the path to get it from start to goal
        Collections.reverse(path);
        return path;
    }
    public boolean inNoFlyZones(LngLat point, LngLatHandler handler, Set<NamedRegion> no_fly_zones, NamedRegion central){
        for(NamedRegion zone : no_fly_zones){
            if((handler.isInRegion(point, zone))){
                return true;
            }
        }
        return false;
    }
}
