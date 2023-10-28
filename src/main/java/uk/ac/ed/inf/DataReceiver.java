package uk.ac.ed.inf;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
public class DataReceiver {
    public static Restaurant[] restaurants;
    public static Order[] orders;
    public static NamedRegion[] no_fly_zones;
    public static NamedRegion central_area;
    public static FlightPath[] order_paths;
    public static final LngLat appleton = new LngLat(-3.186874, 55.944494);

    public static void main(String[] args) {
        if (args.length < 1){
            System.err.println("the base URL must be provided");
            System.exit(1);
        }

        var baseUrl = args[0];
        if (!baseUrl.endsWith("/")){
            baseUrl += "/";
        }

        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            System.err.println("The URL is invalid: " + x);
            System.exit(2);
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            restaurants = mapper.readValue(new URL(baseUrl + scheme_specific.RESTAURANT_URL), Restaurant[].class);
            System.out.println("read all restaurants");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            orders = mapper.readValue(new URL(baseUrl + scheme_specific.ORDER_URL), Order[].class);
            System.out.println("read all orders");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            no_fly_zones = mapper.readValue(new URL(baseUrl + scheme_specific.NO_FLY_ZONE_URL), NamedRegion[].class);
            System.out.println("read all no fly zones");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            central_area = mapper.readValue(new URL(baseUrl + scheme_specific.CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("read all no fly zones");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OrderValidator orderValidator = new OrderValidator();
        PathFinder pathFinder = new PathFinder(appleton);
        for (int i = 0; i < orders.length; i++) {
            orderValidator.validateOrder(orders[i], restaurants);
            if(orders[i].getOrderValidationCode() == OrderValidationCode.NO_ERROR) {
                order_paths[i] = pathFinder.findpath(orders[i], orderValidator.getPizzaRestaurant(orders[i].getPizzasInOrder()[0], restaurants), no_fly_zones, central_area);
                pathFinder.nextpath();
            }
            else{
                order_paths[i] = null;
            }
        }
    }
}
