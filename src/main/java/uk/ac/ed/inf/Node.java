package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Node {
    private Node parent;
    private final LngLat position;
    private final double fCost;
    private final double gCost;
    private final double hCost;
    public Node(LngLat position, Node parent, double fCost, double gCost, double hCost){
        this.position = position;
        this.gCost = gCost;
        this.fCost = fCost;
        this.parent = parent;
        this.hCost = hCost;
    }

    public LngLat getPosition() {
        return position;
    }

    public double getfCost() {
        return fCost;
    }

    public double getgCost() {
        return gCost;
    }

    public double gethCost() {
        return hCost;
    }

    public Node getParent() {
        return parent;
    }

}

