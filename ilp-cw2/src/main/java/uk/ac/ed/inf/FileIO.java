package uk.ac.ed.inf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Stores methods for interacting with files
 */
public class FileIO {

    /** Prefix of the file to write */
    private static final String FILE_PREFIX = "drone";

    /** Suffix of the file to write */
    private static final String FILE_SUFFIX = "geojson";

    /**
     * Writes a flightpath to a GEOJson file.
     *
     * @param dms  a flightpath in collection of DroneMove format.
     * @param date the day the orders were for.
     */
    public static void writeGEOJson(ArrayList<DroneMove> dms, LocalDate date) {
        var out = DroneMove.getMovesAsFC(dms);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Writer fileWriter;
        try {
            fileWriter = new FileWriter(String.format("%s-%s.%s", FILE_PREFIX, date.format(dtf), FILE_SUFFIX),
                    false);
            fileWriter.write(out.toJson());

            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("FAILED TO WRITE FILE");
        }
    }

}
