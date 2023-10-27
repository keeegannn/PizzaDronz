package uk.ac.ed.inf;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandlerTest extends TestCase {
LngLatHandler lngLatHandler = new LngLatHandler();
    public void testDistanceTo() {
        LngLat startPosition = new LngLat(0.0, 0.0);
        LngLat endPosition = new LngLat(3.0, 4.0);
        double distance = lngLatHandler.distanceTo(startPosition, endPosition);
        Assert.assertEquals(5.0, distance, 0.01);
    }

    public void testIsCloseTo() {
        LngLat startPosition = new LngLat(0.0, 0.0);
        LngLat otherPosition = new LngLat(0.0, SystemConstants.DRONE_IS_CLOSE_DISTANCE / 2);
        boolean isClose = lngLatHandler.isCloseTo(startPosition, otherPosition);
        Assert.assertTrue(isClose);
    }

    public void testIsInCentralArea() {
        LngLat pointInside = new LngLat(0.5, 0.5);
        LngLat[] vertices = new LngLat[]{new LngLat(0.0, 1.0),new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.0, 1.0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        boolean isInCentralArea = lngLatHandler.isInCentralArea(pointInside, centralArea);
        assertTrue(isInCentralArea);
    }

    public void testIsInRegion() {
        LngLat pointInside = new LngLat(0.5, 0.5);
        LngLat[] vertices = new LngLat[]{new LngLat(0.0, 1.0),new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.0, 1.0)};
        NamedRegion region = new NamedRegion("Test Region", vertices);
        boolean isInRegion = lngLatHandler.isInRegion(pointInside, region);
        assertTrue(isInRegion);
    }

    public void testNextPosition() {
        LngLat startPosition = new LngLat(0.0, 0.0);
        double angle = 0;
        LngLat nextPos = lngLatHandler.nextPosition(startPosition, angle);
        assertEquals(0.0, nextPos.lat());
        assertEquals(SystemConstants.DRONE_MOVE_DISTANCE, nextPos.lng());
    }
}