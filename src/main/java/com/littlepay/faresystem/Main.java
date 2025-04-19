package com.littlepay.faresystem;

import com.littlepay.faresystem.config.AppConfig;
import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.parser.TapCsvReader;
import com.littlepay.faresystem.parser.TripCsvWriter;
import com.littlepay.faresystem.service.CsvFareRepository;
import com.littlepay.faresystem.service.FareRepository;
import com.littlepay.faresystem.service.TripService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            new Main().run();
        }
        catch (Exception ex) {
            log.error("Error occurred while processing: {}", ex.getMessage());
            System.exit(1);
        }
    }

    private void run() throws IOException {
        String inputFile = AppConfig.get("input.file.name");
        String outputFile = AppConfig.get("output.file.name");
        String tripsFaresConfigFile = AppConfig.get("trip.fares.file.name");

        log.info("Processing taps from input file: {}, Trips fare config file: {}", inputFile, tripsFaresConfigFile);

        FareRepository fareRepository = new CsvFareRepository(tripsFaresConfigFile);
        List<Tap> taps = TapCsvReader.getTapsFromFile(inputFile);

        TripService tripService = new TripService(fareRepository);
        List<Trip> trips = tripService.getTripsFromTaps(taps);

        TripCsvWriter.writeTripsToFile(trips, outputFile);
        log.info("Trip processing completed successfully. Output written to file: {}", outputFile);
    }
}
