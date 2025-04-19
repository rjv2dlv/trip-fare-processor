package com.littlepay.faresystem.parser;

import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.utils.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TripCsvWriter {
    private static final String CSV_HEADER = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status";

    public static void writeTripsToFile(List<Trip> trips, String outputFilename) throws IOException {
        Path outputPath = Paths.get("src/main/resources/" + outputFilename);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath)) {
            bufferedWriter.write(CSV_HEADER);
            bufferedWriter.newLine();

            for (Trip currentTrip : trips) {
                String started = currentTrip.getStarted() != null ? currentTrip.getStarted().format(Constants.DATE_TIME_FORMATTER) : "";
                String finished = currentTrip.getFinished() != null ? currentTrip.getFinished().format(Constants.DATE_TIME_FORMATTER) : "";
                String toStop = currentTrip.getToStopId() != null ? currentTrip.getToStopId() : "";

                String record = String.join(",",
                        started,
                        finished,
                        String.valueOf(currentTrip.getDurationSecs()),
                        currentTrip.getFromStopId(),
                        toStop,
                        "$" + currentTrip.getChargeAmount(),
                        currentTrip.getCompanyId(),
                        currentTrip.getBusId(),
                        currentTrip.getPan(),
                        currentTrip.getStatus()
                );

                bufferedWriter.write(record);
                bufferedWriter.newLine();
            }
        }
    }
}
