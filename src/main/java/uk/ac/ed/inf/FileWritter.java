package uk.ac.ed.inf;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWritter {
    public void writeGeoJson(JsonObject geoJsonObject, String date){
        try {
            //initialize file name
            String filePath = "resultfiles/drone-" + date + ".geojson";

            //create file
            File file = getfile(filePath);

            //create file writer
            FileWriter fileWriter = new FileWriter(file);

            //write flight path to file
            fileWriter.write(String.valueOf(geoJsonObject));

            //close file writer
            fileWriter.close();

            System.out.println("File created and data written successfully.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


        public void writeFlightpathJson(JsonArray flightpath, String date) {
            try {
                // Initialize file name
                String filePath = "resultfiles/flightpath-" + date + ".json";

                // Create file
                File file = getfile(filePath);

                // Create file writer
                FileWriter fileWriter = new FileWriter(file);

                // Write flight path to file
                fileWriter.write(flightpath.toString());

                // Close file writer
                fileWriter.close();

                System.out.println("File created and data written successfully.");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    public void writeDeliveriesJson(JsonArray deliveries, String date){
        try {
            //initialize file name
            String filePath = "resultfiles/deliveries-" + date + ".json";

            //create file
            File file = getfile(filePath);

            //create file writer
            FileWriter fileWriter = new FileWriter(file);

            //write flight path to file
            fileWriter.write(deliveries.toString());

            //close file writer
            fileWriter.close();

            System.out.println("File created and data written successfully.");

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public File getfile(String filePath) throws IOException {
        // Create file
        File file = new File(filePath);

        // Overwrite file if it exists
        if (file.exists()) {
            file.delete();
        }

        // Create parent directories if they don't exist
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // Create the file
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
