package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.CentralRegionVertexOrder;
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
        if (distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE){
            return true;
        }
        else{
            return false;
        }
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
    public LngLat nextPosition(LngLat startPosition, double angle, Double multiplier){
        // Convert degrees to radians
        double angleInRadians = Math.toRadians(angle);

        // Calculate new longitude using the angle and movement distance
        double NewLng = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleInRadians) * multiplier);
        // Calculate new latitude using the angle and movement distance
        double NewLat = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleInRadians) * multiplier);

        // Return the new longitude and latitude as a LngLat object
        return new LngLat(NewLng, NewLat);
    }
    public LngLat findCentreOfRectangle(NamedRegion central){
        double midLat = (central.vertices()[CentralRegionVertexOrder.TOP_RIGHT].lat() + central.vertices()[CentralRegionVertexOrder.BOTTOM_RIGHT].lat())/ 2;
        double midLng = (central.vertices()[CentralRegionVertexOrder.BOTTOM_LEFT].lng() + central.vertices()[CentralRegionVertexOrder.BOTTOM_RIGHT].lng())/ 2;
        return new LngLat(midLng, midLat);
    }
}
