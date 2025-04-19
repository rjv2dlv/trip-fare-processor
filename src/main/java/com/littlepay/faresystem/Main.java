package com.littlepay.faresystem;

import com.littlepay.faresystem.config.AppConfig;
import com.littlepay.faresystem.config.TripFaresConfig;
import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.parser.TapCsvReader;
import com.littlepay.faresystem.parser.TripCsvWriter;
import com.littlepay.faresystem.service.TripService;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        String inputFile = null;
        try {
            inputFile = AppConfig.get("input.file.name");
            String outputFile = AppConfig.get("output.file.name");
            String tripsFaresConfigFile = AppConfig.get("trip.fares.file.name");
            System.out.println(inputFile + " : " + outputFile + " : " + tripsFaresConfigFile);

            TripFaresConfig.loadTripFares(tripsFaresConfigFile);
            TripFaresConfig.loadMaxFaresForEachStop();

            List<Tap> taps = TapCsvReader.getTapsFromFile(inputFile);
            List<Trip> trips = TripService.getTripsFromTaps(taps);
            TripCsvWriter.writeTripsToFile(trips, outputFile);
        }
        catch(IOException ioException) {
            System.out.println("Input file: " + inputFile + " not found");
            System.exit(0);
        }
    }
}
