package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FileWritterTest extends TestCase {
    public static String string = "https://ilp-rest.azurewebsites.net/";
APIinterface apIinterface = new APIinterface();
LngLatHandler lngLatHandler = new LngLatHandler();
JsonSerializer jsonSerializer = new JsonSerializer();

PathFinder pathFinder = new PathFinder();
FileWritter fileWritter = new FileWritter();
String date = "2023-11-15";

    public void testWriteGeoJson() {
        try {
            apIinterface.restGetRequest(new URL(string), date);
            ArrayList<LngLat> full = new ArrayList<>();
            for (int i = 0; i < apIinterface.getRestaurants().length; i++) {
                PathFinder temp;
                //temp = pathCreator.createPath(apIinterface.getAppleton(), apIinterface.getRestaurants()[i].location(), apIinterface.getNoFlyZones(), apIinterface.getCentral_area(), lngLatHandler);
                pathFinder.findPath(apIinterface.getAppleton(), apIinterface.getRestaurants()[i].location(), apIinterface.getNoFlyZones(), lngLatHandler, false, apIinterface.getCentral_area());
                //ArrayList<LngLat> part = pathFinder.getPath();
                //System.out.println(pathFinder.TogeoJson());
                //full.addAll(part);
                //pathFinder.resetPath();
            }
            fileWritter.writeGeoJson(jsonSerializer.toGeoJson(full), date);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void testWriteFlightpathJson() {
    }

    public void testWriteDeliveriesJson() throws MalformedURLException {
        apIinterface.restGetRequest(new URL(string), date);
        //fileWritter.writeDeliveriesJson(jsonSerializer.deliveriesToJson(apIinterface.getOrders()), date);
    }
    private ArrayList<LngLat> deepCopy(ArrayList<LngLat> originalList) {
        ArrayList<LngLat> copy = new ArrayList<>(originalList.size());
        for (LngLat item : originalList) {
            copy.add(new LngLat(item.lat(), item.lng()));
        }
        return copy;
    }
}