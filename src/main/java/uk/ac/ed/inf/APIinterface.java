package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class APIinterface {
    private static final LngLat appleton = new LngLat(-3.186874, 55.944494);
    private static Restaurant[] restaurants;
    private static Order[] orders;
    private static HashSet<NamedRegion> no_fly_zones;
    private static NamedRegion central_area;
    private static boolean alive;
    public void restGetRequest(URL baseUrl, String date){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            restaurants = mapper.readValue(new URL(baseUrl + scheme_specific.RESTAURANT_URL), Restaurant[].class);
            //read the restaurant list
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            orders = mapper.readValue(new URL(baseUrl + scheme_specific.ORDER_URL + "/" + date), Order[].class);
            //read the orders
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            no_fly_zones = mapper.readValue(new URL(baseUrl + scheme_specific.NO_FLY_ZONE_URL), new TypeReference<>() {
            });
            //read all no-fly zones
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            central_area = mapper.readValue(new URL(baseUrl + scheme_specific.CENTRAL_AREA_URL), NamedRegion.class);
            //read the central area
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            alive = mapper.readValue(new URL(baseUrl + scheme_specific.IS_ALIVE_URL), Boolean.class);
            //read alive boolean
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public LngLat getAppleton(){
        return appleton;
    }
    public Restaurant[] getRestaurants(){
        return restaurants;
    }
    public Order[] getOrders(){
        return orders;
    }
    public Set<NamedRegion> getNoFlyZones(){
        return no_fly_zones;
    }
    public NamedRegion getCentral_area(){
        return central_area;
    }
    public boolean isAlive(){
        return alive;
    }
}
