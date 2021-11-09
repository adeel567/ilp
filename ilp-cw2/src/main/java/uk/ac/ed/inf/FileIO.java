package uk.ac.ed.inf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileIO {

    private static Navigation myNavigation = Navigation.getInstance();

    public static void writeGEOJson(ArrayList<DroneMove> dms, LocalDate date){
        var out = DroneMove.getMovesAsFC(dms);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Writer fileWriter = null;
        try {
            fileWriter = new FileWriter(String.format("drone-%s.geojson", date.format(dtf)),
                    false);
            fileWriter.write(out.toJson());

            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("FAILED TO WRITE FILE");
        }
    }

}
