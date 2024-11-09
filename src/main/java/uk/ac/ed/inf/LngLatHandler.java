package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;


public class LngLatHandler implements LngLatHandling{


    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        //using the fact that distance = sqrt((x2-x1)^2 + (y2-y1)^2) calculates the distance between the points
        return Math.sqrt(((endPosition.lat() - startPosition.lat()) * (endPosition.lat() - startPosition.lat())) + ((endPosition.lng() - startPosition.lng()) * (endPosition.lng() - startPosition.lng())));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        //used distanceTo function to check if the drone is startPosition to otherPosition
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        return isInRegion(point, centralArea);
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        int crossings = 0;
        double positionLng = position.lng();
        double positionLat = position.lat();

        for (int i = 0; i < region.vertices().length - 1; i++) {
            LngLat vertex1 = region.vertices()[i];
            LngLat vertex2 = region.vertices()[i + 1];

            double vertex1Lng = vertex1.lng();
            double vertex1Lat = vertex1.lat();
            double vertex2Lng = vertex2.lng();
            double vertex2Lat = vertex2.lat();

            if (((vertex1Lat <= positionLat && positionLat < vertex2Lat)
                    || (vertex2Lat <= positionLat && positionLat < vertex1Lat))
                    && (positionLng < (vertex2Lng - vertex1Lng)
                    * (positionLat - vertex1Lat)
                    / (vertex2Lat - vertex1Lat)
                    + vertex1Lng)) {
                crossings++;
            }
        }

        return crossings % 2 == 1;
    }


    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // Convert degrees to radians
        double angleInRadians = Math.toRadians(angle);

        // Calculate new longitude using the angle and movement distance
        double NewLng = startPosition.lng() + SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleInRadians);
        // Calculate new latitude using the angle and movement distance
        double NewLat = startPosition.lat() + SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleInRadians);

        // Return the new longitude and latitude as a LngLat object
        return new LngLat(NewLng, NewLat);
    }
    public LngLat findClosestPoint(LngLat position, NamedRegion central){
        //intializes the closest point and distance
        LngLat closestpoint = central.vertices()[0];
        double lowestdistance = distanceTo(position, closestpoint);
        //loops through every vertice in the area and finds the closes point on the perimeter of central area
        for(int i = 0; i < 3; i++){
            if(distanceTo(position, findClosestPointOnEdge(position, central.vertices()[i], central.vertices()[i + 1])) < lowestdistance){
                lowestdistance = distanceTo(position, findClosestPointOnEdge(position, central.vertices()[i], central.vertices()[i + 1]));
                closestpoint = findClosestPointOnEdge(position, central.vertices()[i], central.vertices()[i + 1]);
            }
            if(distanceTo(position, central.vertices()[i]) < lowestdistance){
                lowestdistance = distanceTo(position, central.vertices()[i]);
                closestpoint = central.vertices()[i];
            }
        }
        return closestpoint;
    }
    public LngLat findClosestPointOnEdge(LngLat point, LngLat start, LngLat end) {
        // Extracting coordinates of the start point
        double x1 = start.lng();
        double y1 = start.lat();

        // Extracting coordinates of the end point
        double x2 = end.lng();
        double y2 = end.lat();

        // Extracting coordinates of the given point
        double x0 = point.lng();
        double y0 = point.lat();

        double x, y; // Variables to store the coordinates of the closest point on the edge

        // Calculating the parameter 'u' to determine the position of the closest point on the edge
        double u = ((x0 - x1) * (x2 - x1) + (y0 - y1) * (y2 - y1)) / ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

        // Checking different cases based on the value of 'u'
        if (u < 0) {
            // The closest point is before the start of the edge, so set it to the start point
            x = x1;
            y = y1;
        } else if (u > 1) {
            // The closest point is after the end of the edge, so set it to the end point
            x = x2;
            y = y2;
        } else {
            // The closest point is between the start and end points, so interpolate the coordinates
            x = x1 + u * (x2 - x1);
            y = y1 + u * (y2 - y1);
        }

        // Creating a new LngLat object with the coordinates of the closest point and returning it
        return new LngLat(x, y);
    }
}
