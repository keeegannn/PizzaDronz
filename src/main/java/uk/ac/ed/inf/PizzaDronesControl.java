package uk.ac.ed.inf;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

@SpringBootApplication
public class PizzaDronesControl {

    public static void main(String[] args) throws MalformedURLException {
        SpringApplication.run(PizzaDronesControl.class, args);
        System.out.println("Program has started");
        if (args.length < 2) {
            System.err.println("the base URL and Date must be provided");
            System.exit(1);
        }
        var baseUrl = args[1];
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String date = args[0];
        boolean validdate = true;
        if (!(date.length() == 10)) {
            validdate = false;
        } else {
            for (int i = 0; i < 10; i++) {
                if (i == 4 || i == 7) {
                    if (date.charAt(i) != '-') {
                        validdate = false;
                    }
                } else {
                    char x = date.charAt(i);
                    if (!(Character.isDigit(x))) {
                        validdate = false;
                    }
                }
            }
        }
        if (validdate) {
            System.out.println("Date is formatted correctly");
        } else {
            System.err.println("Date entered was of invalid formatting");
            System.exit(2);
        }
        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            System.err.println("The URL is invalid: " + x);
            System.exit(3);
        }
        APIinterface apIinterface = new APIinterface();
        apIinterface.restGetRequest(new URL(baseUrl), args[0]);
        if (apIinterface.isAlive()) {
            //Initialize relevant Objects
            OrderValidator orderValidator = new OrderValidator();
            Order[] orders = apIinterface.getOrders();
            JsonSerializer jsonSerializer = new JsonSerializer();
            JsonArray instructions = new JsonArray();
            JsonArray deliveries = new JsonArray();
            ArrayList<LngLat> path = new ArrayList<>();
            FileWritter fileWritter = new FileWritter();
            PathFinder pathFinder = new PathFinder();
            LngLatHandler handler = new LngLatHandler();
            LngLat appletonPoint = apIinterface.getAppleton();
            //Validate all orders and make flight paths for valid ones
            for (Order order : orders) {
                orderValidator.validateOrder(order, apIinterface.getRestaurants());
                if (order.getOrderValidationCode() == OrderValidationCode.NO_ERROR && order.getOrderStatus() != OrderStatus.INVALID) {
                    order.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
                    ArrayList<LngLat> temp = pathFinder.findPath(appletonPoint, orderValidator.getPizzaRestaurant(order.getPizzasInOrder()[0], apIinterface.getRestaurants()).location(), apIinterface.getNoFlyZones(), handler, false, apIinterface.getCentral_area());
                    instructions.addAll(jsonSerializer.pathToJson(temp, order.getOrderNo(), false));
                    path.addAll(temp);
                    //finds the endpoint of the previous path
                    LngLat thirdStart = temp.get(temp.size()-1);
                    //checks if restaurant is within central area
                    if(!handler.isInCentralArea(thirdStart, apIinterface.getCentral_area())) {
                        ArrayList<LngLat> temp2 = pathFinder.findPath(thirdStart, handler.findClosestPoint(temp.get(temp.size() - 1), apIinterface.getCentral_area()), apIinterface.getNoFlyZones(), handler, true, apIinterface.getCentral_area());
                        //adds it to the total path
                        path.addAll(temp2);
                        //finds the endpoint of the path to central
                        thirdStart = temp2.get(temp2.size()-1);
                        instructions.addAll(jsonSerializer.pathToJson(temp2, order.getOrderNo(), true));
                    }
                    ArrayList<LngLat> temp3 = pathFinder.findPath(thirdStart, apIinterface.getAppleton(), apIinterface.getNoFlyZones(), handler, false, apIinterface.getCentral_area());
                    instructions.addAll(jsonSerializer.pathToJson(temp3, order.getOrderNo(), false));
                    path.addAll(temp3);
                    appletonPoint = path.get(path.size()-1);
                    order.setOrderStatus(OrderStatus.DELIVERED);
                }
                deliveries.add(jsonSerializer.deliveriesToJson(order));
            }
            JsonObject geoJson = jsonSerializer.toGeoJson(path);
            //Write all Json Serialized objects to the necessary files
            fileWritter.writeDeliveriesJson(deliveries, args[0]);
            fileWritter.writeGeoJson(geoJson, args[0]);
            fileWritter.writeFlightpathJson(instructions, args[0]);
            System.out.println("Program Exited");
            System.exit(4);
        }
        else{
            System.err.println("Rest API is currently not available");
            System.exit(5);
        }
    }
}
