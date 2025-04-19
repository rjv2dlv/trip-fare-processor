package com.littlepay.faresystem.service;

import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.model.TripStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class TripService {

    private final FareRepository fareRepository;

    public TripService(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    public List<Trip> getTripsFromTaps(List<Tap> taps) {
        List<Trip> allTrips = new ArrayList<>();
        Map<String, List<Tap>> sortedTapsByPanMap = TapOrganiser.groupAndSortTapsByPan(taps);
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

    private Trip createTrip(Tap tapOn, Tap tapOff) {
        if(tapOff == null) {
            return createIncompleteTrip(tapOn);
        } else if(tapOn.getStopId().equals(tapOff.getStopId())) {
            return createCancelledTrip(tapOn, tapOff);
        } else {
            return createCompletedTrip(tapOn, tapOff);
        }
    }

    private Trip createIncompleteTrip(Tap tapOn) {
        Trip incompleteTrip = baseTrip(tapOn);
        incompleteTrip.setChargeAmount(fareRepository.getMaxFare(tapOn.getStopId()));
        incompleteTrip.setStatus(TripStatus.INCOMPLETE);
        incompleteTrip.setToStopId("");
        return incompleteTrip;
    }

    private Trip createCancelledTrip(Tap tapOn, Tap tapOff) {
        Trip cancelledTrip = baseTrip(tapOn);
        cancelledTrip.setFinished(tapOff.getDateTimeUTC());
        cancelledTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
        cancelledTrip.setChargeAmount(BigDecimal.ZERO);
        cancelledTrip.setStatus(TripStatus.CANCELLED);
        cancelledTrip.setToStopId(tapOff.getStopId());
        return cancelledTrip;
    }

    private Trip createCompletedTrip(Tap tapOn, Tap tapOff) {
        Trip completedTrip = baseTrip(tapOn);
        completedTrip.setFinished(tapOff.getDateTimeUTC());
        completedTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
        completedTrip.setChargeAmount(fareRepository.getFare(tapOn.getStopId(), tapOff.getStopId()));
        completedTrip.setStatus(TripStatus.COMPLETED);
        completedTrip.setToStopId(tapOff.getStopId());
        return completedTrip;
    }

    private Trip baseTrip(Tap tapOn) {
        Trip baseTrip = new Trip();
        baseTrip.setStarted(tapOn.getDateTimeUTC());
        baseTrip.setFromStopId(tapOn.getStopId());
        baseTrip.setCompanyId(tapOn.getCompanyId());
        baseTrip.setBusId(tapOn.getBusId());
        baseTrip.setPan(tapOn.getPan());
        return baseTrip;
    }
}
