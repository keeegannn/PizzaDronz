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
        LngLat botRightLngLat = region.vertices()[CentralRegionVertexOrder.BOTTOM_RIGHT];
        LngLat topLeftLngLat = region.vertices()[CentralRegionVertexOrder.TOP_LEFT];
        LngLat topRightLngLat = region.vertices()[CentralRegionVertexOrder.TOP_RIGHT];
        LngLat botLeftLngLat = region.vertices()[CentralRegionVertexOrder.BOTTOM_LEFT];
        return position.lat() >= botLeftLngLat.lat() && position.lat() <= topLeftLngLat.lat() && position.lat() >= botLeftLngLat.lng() && position.lng() <= botRightLngLat.lng();
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
}
