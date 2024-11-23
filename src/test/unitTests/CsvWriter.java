package test.unitTests;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {
    public static void writeResults(String csvFile, String operation, int numObjects, long duration, double cpuUsage, long memoryUsage) {
        try (FileWriter writer = new FileWriter(csvFile, true)) { // Append mode
            writer.write(operation + "," + numObjects + "," + duration + "," + cpuUsage + "," + memoryUsage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
