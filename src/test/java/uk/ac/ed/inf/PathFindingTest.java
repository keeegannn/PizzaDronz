package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PathFindingTest {
    public static Restaurant[] restaurants;
    public static String string = "https://ilp-rest.azurewebsites.net/";

    public static void main(String[] args) {
        // Define the REST API URL to fetch data (replace with the actual API URL)
        String apiUrl = "https://ilp-rest.azurewebsites.net/"; // Replace with the actual API URL


        LngLatHandler lngLatHandler = new LngLatHandler();
        PathCreator pathCreator = new PathCreator();

        try {
            // Make an HTTP request to the REST AP  I and fetch the JSON data
            ObjectMapper mapper = new ObjectMapper();
            restaurants = fetchRestaurants(apiUrl + scheme_specific.RESTAURANT_URL, mapper);

            // Extract data from the response
            LngLat start = new LngLat(-3.186298689756569, 55.94437662228796);
            LngLat goal = new LngLat(-3.1972902753465746, 55.945141911291984);
            LngLat cent1 = new LngLat(-3.192473, 55.946233); //TOP_LEFT
            LngLat cent2 = new LngLat(-3.192473, 55.942617); //BOTTOM_LEFT
            LngLat cent3 = new LngLat(-3.184319, 55.942617); //BOTTOM_RIGHT
            LngLat cent4 = new LngLat(-3.184319, 55.946233); //TOP_RIGHT



            Set<LngLat> closed = new HashSet<>();
            HashSet<LngLat> openset = new HashSet<>();
            openset.add(start);

            Set<NamedRegion> noFlyZones = fetchNoFlyZones(string + scheme_specific.NO_FLY_ZONE_URL, mapper);
            PathFinder[] pathfinders = new PathFinder[restaurants.length];
            NamedRegion centralArea = fetchCentralArea(string + scheme_specific.CENTRAL_AREA_URL, mapper);
            // Call the findPath method to calculate the path and angles
            System.out.println("Before");
            for(int i = 0; i < restaurants.length; i++) {
                PathFinder pathFinder = new PathFinder();
                pathCreator.createPath(start, restaurants[i].location(), noFlyZones, centralArea, pathFinder, lngLatHandler);
                pathfinders[i] = pathFinder;
            }
            //pathFinder.findPath(restaurants[0].location(),lngLatHandler.findCentreOfRectangle(centralArea), noFlyZones, lngLatHandler, true, centralArea);
            //pathFinder.findPath(destination, start, noFlyZones, lngLatHandler, false, centralArea);
            System.out.println("after");
            for (PathFinder pathfinder : pathfinders) {
                System.out.println(pathfinder.ToJson());
            }

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An error occurred while fetching data: " + e.getMessage());
        }
    }

    private static Restaurant[] fetchRestaurants(String apiUrl, ObjectMapper mapper) throws IOException {
        URL restaurantUrl = new URL(apiUrl);
        return mapper.readValue(restaurantUrl, Restaurant[].class);
    }

    private static Set<NamedRegion> fetchNoFlyZones(String apiUrl, ObjectMapper mapper) throws IOException {
        TypeReference<HashSet<NamedRegion>> typeReference = new TypeReference<HashSet<NamedRegion>>() {};
        URL noFlyZoneUrl = new URL(apiUrl);
        return mapper.readValue(noFlyZoneUrl, typeReference);
    }
    private static NamedRegion fetchCentralArea(String apiUrl, ObjectMapper mapper) throws IOException {
        URL centralArea = new URL(apiUrl);
        return mapper.readValue(centralArea, NamedRegion.class);
    }
}
