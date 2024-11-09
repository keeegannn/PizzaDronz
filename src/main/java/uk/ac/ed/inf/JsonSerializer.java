package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import java.util.ArrayList;

public class JsonSerializer {
    public JsonObject toGeoJson(ArrayList<LngLat> path) {
        JsonObject geoJson = new JsonObject();

        // Create FeatureCollection object
        geoJson.addProperty("type", "FeatureCollection");

        // Create features array
        JsonArray features = new JsonArray();

        // Create Feature object
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        JsonObject properties = new JsonObject();
        feature.add("properties", properties);

        // Create LineString geometry
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");

        JsonArray coordinates = new JsonArray();
        for (LngLat point : path) {
            JsonArray pointArray = new JsonArray();
            pointArray.add(point.lng());
            pointArray.add(point.lat());
            coordinates.add(pointArray);
        }

        geometry.add("coordinates", coordinates);
        feature.add("geometry", geometry);

        // Add the Feature to the features array
        features.add(feature);

        // Add the features array to the GeoJSON
        geoJson.add("features", features);

        return geoJson;
    }
    public JsonArray pathToJson(ArrayList<LngLat> path, String orderNo, boolean IsToCentral){
        JsonArray moves = new JsonArray();
        int x = path.size() - 1;
        for(int i = 0; i < x; i++){
            JsonObject move = new JsonObject();
            move.addProperty("orderNo", orderNo);
            move.addProperty("fromLongitude", path.get(i).lng());
            move.addProperty("fromLatitude", path.get(i).lat());
            double angle = calculateAngle(path.get(i), path.get(i + 1));
            move.addProperty("angle", angle);
            move.addProperty("toLongitude", path.get(i + 1).lng());
            move.addProperty("toLatitude", path.get(i + 1).lat());
            moves.add(move);
        }
        if(!IsToCentral) {
            JsonObject move = new JsonObject();
            move.addProperty("orderNo", orderNo);
            move.addProperty("fromLongitude", path.get(x).lng());
            move.addProperty("fromLatitude", path.get(x).lat());
            double angle = 999.0;
            move.addProperty("angle", angle);
            move.addProperty("toLongitude", path.get(x).lng());
            move.addProperty("toLatitude", path.get(x).lat());
            moves.add(move);
        }
        return moves;
    }
    private double calculateAngle(LngLat from, LngLat to) {
            double angle = Math.toDegrees(Math.atan2(to.lat() - from.lat(), to.lng() - from.lng()));
            double roundedAngle = Math.round(angle * 10.0) / 10.0; // Round to one decimal point
            return roundedAngle >= 0 ? roundedAngle : 360 + roundedAngle;
    }
    public JsonObject deliveriesToJson(Order order){
            JsonObject delivery = new JsonObject();

            delivery.addProperty("orderNo", order.getOrderNo());

            delivery.addProperty("orderStatus", order.getOrderStatus().toString());

            delivery.addProperty("orderValidationCode", order.getOrderValidationCode().toString());

            delivery.addProperty("costInPence", order.getPriceTotalInPence());

        return delivery;
    }
}
