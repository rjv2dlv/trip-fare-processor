package com.littlepay.faresystem.service;

import com.littlepay.faresystem.config.TripFaresConfig;
import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class TripService {
    public static List<Trip> getTripsFromTaps(List<Tap> taps) {
        List<Trip> allTrips = new ArrayList<>();
        Map<String, List<Tap>> sortedTapsByPanMap = getSortedTapsByPan(taps);
        for(String pan : sortedTapsByPanMap.keySet()) {
            List<Tap> currentPanTaps = sortedTapsByPanMap.get(pan);
            for(int i = 0; i < currentPanTaps.size(); i ++) {
                Tap currentTap = currentPanTaps.get(i);
                if(! currentTap.getTapType().equals("ON"))
                    continue;

                Tap tapOn = currentTap;
                Tap tapOff = null;

                // Current tapType is tap ON.
                if(i + 1 < currentPanTaps.size() && currentPanTaps.get(i + 1).getTapType().equals("OFF")) {
                    tapOff = currentPanTaps.get(i + 1);
                    i ++;
                }

                allTrips.add(createTrip(tapOn, tapOff));
            }
        }

        return allTrips;
    }

    private static Trip createTrip(Tap tapOn, Tap tapOff) {
        Trip thisTrip = new Trip();
        thisTrip.setStarted(tapOn.getDateTimeUTC());
        thisTrip.setFromStopId(tapOn.getStopId());

        if(tapOff == null) {
            thisTrip.setChargeAmount(TripFaresConfig.getMaxTripFare(tapOn.getStopId()));
            thisTrip.setStatus("INCOMPLETE");
            thisTrip.setToStopId("");
        }
        else if(tapOn.getStopId().equals(tapOff.getStopId())) {
            thisTrip.setFinished(tapOff.getDateTimeUTC());
            thisTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
            thisTrip.setChargeAmount(BigDecimal.ZERO);
            thisTrip.setStatus("CANCELLED");
            thisTrip.setToStopId(tapOff.getStopId());
        }
        else {
            thisTrip.setFinished(tapOff.getDateTimeUTC());
            thisTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
            thisTrip.setChargeAmount(TripFaresConfig.getFare(tapOn.getStopId(), tapOff.getStopId()));
            thisTrip.setStatus("COMPLETED");
            thisTrip.setToStopId(tapOff.getStopId());
        }

        thisTrip.setCompanyId(tapOn.getCompanyId());
        thisTrip.setBusId(tapOn.getBusId());
        thisTrip.setPan(tapOn.getPan());

        return thisTrip;
    }

    private static Map<String, List<Tap>> getSortedTapsByPan(List<Tap> taps) {
        Map<String, List<Tap>> sortedTapsByPanMap = new HashMap<>();
        for(Tap currentTap : taps) {
            List<Tap> currentPanTaps = sortedTapsByPanMap.getOrDefault(currentTap.getPan(), new ArrayList<>());
            currentPanTaps.add(currentTap);
            sortedTapsByPanMap.put(currentTap.getPan(), currentPanTaps);
        }

        for(List<Tap> tapList : sortedTapsByPanMap.values()) {
            tapList.sort(Comparator.comparing(Tap::getDateTimeUTC));
        }

        return sortedTapsByPanMap;
    }
}
