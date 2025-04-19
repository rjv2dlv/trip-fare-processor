package com.littlepay.faresystem.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CsvFareRepository implements FareRepository {

    private static final Map<String, Map<String, BigDecimal>> tripFaresMap = new HashMap<>();
    private static final Map<String, BigDecimal> tripMaxFares = new HashMap<>();

    public CsvFareRepository(String tripFareFile) {
        loadTripFares(tripFareFile);
        loadMaxFares();
    }

    private void loadTripFares(String tripsFareFile) {
        try (InputStream inputStream = CsvFareRepository.class.getClassLoader().getResourceAsStream(tripsFareFile)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Trip fares file not found under resources: " + tripsFareFile);
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                bufferedReader.readLine();

                int tripRecordNumber = 0;
                String tripRecord;
                while ((tripRecord = bufferedReader.readLine()) != null) {
                    String[] tripRecordFields = tripRecord.split(",");
                    String sourceStop = tripRecordFields[0].trim();
                    String destinationStop = tripRecordFields[1].trim();
                    BigDecimal tripFare = new BigDecimal(tripRecordFields[2].trim());

                    addFare(sourceStop, destinationStop, tripFare);       // Add source to destination fare.
                    addFare(destinationStop, sourceStop, tripFare);       // Add destination to source fare.
                    tripRecordNumber ++;
                }
                log.info("Added fares for {} source and destinations.", tripRecordNumber);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading trip fares file: " + e.getMessage(), e);
        }
    }

    private void addFare(String source, String destination, BigDecimal tripFare) {
        Map<String, BigDecimal> currentSourceTrip = tripFaresMap.getOrDefault(source, new HashMap<>());
        currentSourceTrip.put(destination, tripFare);
        tripFaresMap.put(source, currentSourceTrip);
    }

    private void loadMaxFares() {
        for (String currentStop : tripFaresMap.keySet()) {
            Map<String, BigDecimal> currentStopFares = tripFaresMap.get(currentStop);

            BigDecimal maxFare = BigDecimal.ZERO;
            for (BigDecimal fare : currentStopFares.values()) {
                maxFare = (maxFare.compareTo(fare) < 0) ? fare : maxFare;
            }
            tripMaxFares.put(currentStop, maxFare);
        }
        log.info("Max fares added for {} stops", tripMaxFares.size());
    }

    @Override
    public BigDecimal getFare(String sourceStop, String destinationStop) {
        return sourceStop.equals(destinationStop) ? BigDecimal.ZERO : tripFaresMap.get(sourceStop).get(destinationStop);
    }

    @Override
    public BigDecimal getMaxFare(String stop) {
        return tripMaxFares.get(stop);
    }
}
