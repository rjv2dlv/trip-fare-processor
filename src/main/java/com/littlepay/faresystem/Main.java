package com.littlepay.faresystem;

import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Read application.properties to get the configuration (using a static object)
        // 2. Get the input and output file names and path from step 1.

        String inputFile = "taps.csv";
        List<Tap> taps = getTapsFromFile(inputFile);
        List<Trip> trips = getTripsFromTaps(taps);
        writeTripsToFile(trips, "trips.csv");
    }

    private static void writeTripsToFile(List<Trip> trips, String outputFilename) {
        // 1. Write the trips to csv files with the right output.
    }

    private static List<Trip> getTripsFromTaps(List<Tap> taps) {
        // 1. Iterate over each tap
        // 2. For each PAN, sort the trips based on timestamps
        // 3. Update trip status for each trip (COMPLETE, INCOMPLETE, CANCELLED)
        // 4. Return the list of trips across all PANs
        return new ArrayList<>();
    }

    private static List<Tap> getTapsFromFile(String inputFile) {
        // 1. Read the file
        // 2. Create a Tap record for each line in the file
        // 3. Return the list of Taps with the right ON and OFF status for taps
        return new ArrayList<>();
    }
}
