package com.littlepay.faresystem;

import com.littlepay.faresystem.config.AppConfig;
import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.parser.TapCsvReader;
import com.littlepay.faresystem.parser.TripCsvWriter;
import com.littlepay.faresystem.service.CsvFareRepository;
import com.littlepay.faresystem.service.FareRepository;
import com.littlepay.faresystem.service.TripService;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String inputFile = null;
        try {
            inputFile = AppConfig.get("input.file.name");
            String outputFile = AppConfig.get("output.file.name");
            String tripsFaresConfigFile = AppConfig.get("trip.fares.file.name");
            System.out.println(inputFile + " : " + outputFile + " : " + tripsFaresConfigFile);

            FareRepository fareRepository = new CsvFareRepository(tripsFaresConfigFile);

            List<Tap> taps = TapCsvReader.getTapsFromFile(inputFile);

            TripService tripService = new TripService(fareRepository);
            List<Trip> trips = tripService.getTripsFromTaps(taps);

            TripCsvWriter.writeTripsToFile(trips, outputFile);
        }
        catch(IOException ioException) {
            System.out.println("Input file: " + inputFile + " not found");
            System.exit(0);
        }
    }
}
