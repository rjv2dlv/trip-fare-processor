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
    public static void writeTripsToFile(List<Trip> trips, String outputFilename) throws IOException {
        Path outputPath = Paths.get("src/main/resources/" + outputFilename);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath)) {
            bufferedWriter.write("Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status");
            bufferedWriter.newLine();

            for(Trip currentTrip : trips) {
                String started = currentTrip.getStarted() != null ? currentTrip.getStarted().format(Constants.DATE_TIME_FORMATTER) : "";
                String finished = currentTrip.getFinished() != null ? currentTrip.getFinished().format(Constants.DATE_TIME_FORMATTER) : "";
                String toStop = currentTrip.getToStopId() != null ? currentTrip.getToStopId() : "";

                StringBuilder tripStringBuilder = new StringBuilder();
                tripStringBuilder.append(started).append(",");
                tripStringBuilder.append(finished).append(",");
                tripStringBuilder.append(currentTrip.getDurationSecs()).append(",");
                tripStringBuilder.append(currentTrip.getFromStopId()).append(",");
                tripStringBuilder.append(toStop).append(",");
                tripStringBuilder.append("$").append(currentTrip.getChargeAmount()).append(",");
                tripStringBuilder.append(currentTrip.getCompanyId()).append(",");
                tripStringBuilder.append(currentTrip.getBusId()).append(",");
                tripStringBuilder.append(currentTrip.getPan()).append(",");
                tripStringBuilder.append(currentTrip.getStatus());

                bufferedWriter.write(tripStringBuilder.toString());
                bufferedWriter.newLine();
            }
        }
    }
}
