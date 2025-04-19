package com.littlepay.faresystem.config;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TripFaresConfig {
    private static final Map<String, Map<String, BigDecimal>> tripFaresMap = new HashMap<>();
    private static final Map<String, BigDecimal> tripMaxFares = new HashMap<>();

    public static void loadTripFares(String tripsFareFile) throws FileNotFoundException {
        try (InputStream inputStream = TripFaresConfig.class.getClassLoader().getResourceAsStream(tripsFareFile)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Trip fares file not found under resources: " + tripsFareFile);
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                bufferedReader.readLine();

                String tripRecord;
                while ((tripRecord = bufferedReader.readLine()) != null) {
                    String[] tripRecordFields = tripRecord.split(",");
                    String sourceStop = tripRecordFields[0].trim();
                    String destinationStop = tripRecordFields[1].trim();
                    BigDecimal tripFare = new BigDecimal(tripRecordFields[2].trim());

                    updateCurrentTripFare(sourceStop, destinationStop, tripFare);       // Add source to destination fare.
                    updateCurrentTripFare(destinationStop, sourceStop, tripFare);       // Add destination to source fare.
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error reading trip fares file: " + e.getMessage(), e);
        }
    }

    private static void updateCurrentTripFare(String source, String destination, BigDecimal tripFare) {
        Map<String, BigDecimal> currentSourceTrip = tripFaresMap.getOrDefault(source, new HashMap<>());
        currentSourceTrip.put(destination, tripFare);
        tripFaresMap.put(source, currentSourceTrip);
    }

    public static void loadMaxFaresForEachStop() {
        for (String currentStop : tripFaresMap.keySet()) {
            Map<String, BigDecimal> currentStopFares = tripFaresMap.get(currentStop);

            BigDecimal maxFare = BigDecimal.ZERO;
            for (BigDecimal fare : currentStopFares.values()) {
                maxFare = (maxFare.compareTo(fare) < 0) ? fare : maxFare;
            }
            tripMaxFares.put(currentStop, maxFare);
        }
    }

    public static BigDecimal getFare(String sourceStop, String destinationStop) {
        return sourceStop.equals(destinationStop) ? BigDecimal.ZERO : tripFaresMap.get(sourceStop).get(destinationStop);
    }

    public static BigDecimal getMaxTripFare(String stop) {
        return tripMaxFares.get(stop);
    }
}
