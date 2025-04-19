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

    FareRepository fareRepository;

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
        Trip thisTrip = new Trip();
        thisTrip.setStarted(tapOn.getDateTimeUTC());
        thisTrip.setFromStopId(tapOn.getStopId());

        if(tapOff == null) {
            thisTrip.setChargeAmount(fareRepository.getMaxFare(tapOn.getStopId()));
            thisTrip.setStatus(TripStatus.INCOMPLETE);
            thisTrip.setToStopId("");
        }
        else if(tapOn.getStopId().equals(tapOff.getStopId())) {
            thisTrip.setFinished(tapOff.getDateTimeUTC());
            thisTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
            thisTrip.setChargeAmount(BigDecimal.ZERO);
            thisTrip.setStatus(TripStatus.CANCELLED);
            thisTrip.setToStopId(tapOff.getStopId());
        }
        else {
            thisTrip.setFinished(tapOff.getDateTimeUTC());
            thisTrip.setDurationSecs(Duration.between(tapOn.getDateTimeUTC(), tapOff.getDateTimeUTC()).getSeconds());
            thisTrip.setChargeAmount(fareRepository.getFare(tapOn.getStopId(), tapOff.getStopId()));
            thisTrip.setStatus(TripStatus.COMPLETED);
            thisTrip.setToStopId(tapOff.getStopId());
        }

        thisTrip.setCompanyId(tapOn.getCompanyId());
        thisTrip.setBusId(tapOn.getBusId());
        thisTrip.setPan(tapOn.getPan());

        return thisTrip;
    }
}
