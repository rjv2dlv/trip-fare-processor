package com.littlepay.faresystem.service;

import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.model.Trip;
import com.littlepay.faresystem.model.TripStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void testCompletedTrip() {
        Tap tapOn = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tapOff = createTap(1, LocalDateTime.parse("2023-01-22T13:05:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");

        when(fareRepository.getFare("Stop1", "Stop2")).thenReturn(new BigDecimal(3.25));
        List<Tap> taps = List.of(tapOn, tapOff);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(1, allTrips.size());

        Trip trip = allTrips.get(0);
        Assertions.assertEquals("Stop1", trip.getFromStopId());
        Assertions.assertEquals("Stop2", trip.getToStopId());
        Assertions.assertEquals(new BigDecimal(3.25), trip.getChargeAmount());
        Assertions.assertEquals(300, trip.getDurationSecs());
        Assertions.assertEquals(TripStatus.COMPLETED, trip.getStatus());
    }

    @Test
    void testInCompleteTrip() {
        Tap tapOn = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");

        when(fareRepository.getMaxFare("Stop1")).thenReturn(new BigDecimal(7.30));
        List<Tap> taps = List.of(tapOn);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(1, allTrips.size());

        Trip trip = allTrips.get(0);
        Assertions.assertEquals("Stop1", trip.getFromStopId());
        Assertions.assertEquals("", trip.getToStopId());
        Assertions.assertEquals(new BigDecimal(7.30), trip.getChargeAmount());
        Assertions.assertEquals(0, trip.getDurationSecs());
        Assertions.assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
    }

    @Test
    void testCancelledTrip() {
        Tap tapOn = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tapOff = createTap(1, LocalDateTime.parse("2023-01-22T13:02:00"), "OFF", "Stop1", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tapOn, tapOff);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(1, allTrips.size());

        Trip trip = allTrips.get(0);
        Assertions.assertEquals("Stop1", trip.getFromStopId());
        Assertions.assertEquals("Stop1", trip.getToStopId());
        Assertions.assertEquals(BigDecimal.ZERO, trip.getChargeAmount());
        Assertions.assertEquals(120, trip.getDurationSecs());
        Assertions.assertEquals(TripStatus.CANCELLED, trip.getStatus());
    }

    @Test
    void testMultipleTapsForSinglePanTrip() {
        Tap tapOn1 = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tapOff1 = createTap(2, LocalDateTime.parse("2023-01-22T13:05:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");

        Tap tapOn2 = createTap(3, LocalDateTime.parse("2023-01-22T16:00:00"), "ON", "Stop2", "Company1", "Bus37", "5500005555555559");
        Tap tapOff2 = createTap(4, LocalDateTime.parse("2023-01-22T16:04:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");

        Tap tapOn3 = createTap(5, LocalDateTime.parse("2023-01-22T17:00:00"), "ON", "Stop3", "Company1", "Bus37", "5500005555555559");

        when(fareRepository.getFare("Stop1", "Stop2")).thenReturn(new BigDecimal(3.25));
        when(fareRepository.getMaxFare("Stop3")).thenReturn(new BigDecimal(7.30));
        List<Tap> taps = List.of(tapOn1, tapOff1, tapOn2, tapOff2, tapOn3);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(3, allTrips.size());

        Trip trip1 = allTrips.get(0);
        Assertions.assertEquals("Stop1", trip1.getFromStopId());
        Assertions.assertEquals("Stop2", trip1.getToStopId());
        Assertions.assertEquals(new BigDecimal(3.25), trip1.getChargeAmount());
        Assertions.assertEquals(300, trip1.getDurationSecs());
        Assertions.assertEquals(TripStatus.COMPLETED, trip1.getStatus());

        Trip trip2 = allTrips.get(1);
        Assertions.assertEquals("Stop2", trip2.getFromStopId());
        Assertions.assertEquals("Stop2", trip2.getToStopId());
        Assertions.assertEquals(BigDecimal.ZERO, trip2.getChargeAmount());
        Assertions.assertEquals(240, trip2.getDurationSecs());
        Assertions.assertEquals(TripStatus.CANCELLED, trip2.getStatus());

        Trip trip3 = allTrips.get(2);
        Assertions.assertEquals("Stop3", trip3.getFromStopId());
        Assertions.assertEquals("", trip3.getToStopId());
        Assertions.assertEquals(new BigDecimal(7.30), trip3.getChargeAmount());
        Assertions.assertEquals(0, trip3.getDurationSecs());
        Assertions.assertEquals(TripStatus.INCOMPLETE, trip3.getStatus());
    }

    @Test
    void testMultipleTapsForMultiplePansTrip() {
        Tap tapOn1 = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tapOff1 = createTap(2, LocalDateTime.parse("2023-01-22T13:05:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");

        Tap tapOn2 = createTap(3, LocalDateTime.parse("2023-01-22T16:00:00"), "ON", "Stop2", "Company1", "Bus37", "4111111111111111");
        Tap tapOff2 = createTap(4, LocalDateTime.parse("2023-01-22T16:04:00"), "OFF", "Stop2", "Company1", "Bus37", "4111111111111111");

        when(fareRepository.getFare("Stop1", "Stop2")).thenReturn(new BigDecimal(3.25));
        List<Tap> taps = List.of(tapOn1, tapOff1, tapOn2, tapOff2);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(2, allTrips.size());

        Trip trip1 = allTrips.get(0);
        Assertions.assertEquals("5500005555555559", trip1.getPan());
        Assertions.assertEquals("Stop1", trip1.getFromStopId());
        Assertions.assertEquals("Stop2", trip1.getToStopId());
        Assertions.assertEquals(new BigDecimal(3.25), trip1.getChargeAmount());
        Assertions.assertEquals(300, trip1.getDurationSecs());
        Assertions.assertEquals(TripStatus.COMPLETED, trip1.getStatus());

        Trip trip2 = allTrips.get(1);
        Assertions.assertEquals("4111111111111111", trip2.getPan());
        Assertions.assertEquals("Stop2", trip2.getFromStopId());
        Assertions.assertEquals("Stop2", trip2.getToStopId());
        Assertions.assertEquals(BigDecimal.ZERO, trip2.getChargeAmount());
        Assertions.assertEquals(240, trip2.getDurationSecs());
        Assertions.assertEquals(TripStatus.CANCELLED, trip2.getStatus());
    }

    @Test
    void testOutOfOrderTapsTrip() {
        Tap tapOff1 = createTap(2, LocalDateTime.parse("2023-01-22T13:05:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");
        Tap tapOn1 = createTap(1, LocalDateTime.parse("2023-01-22T13:00:00"), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");

        Tap tapOff2 = createTap(4, LocalDateTime.parse("2023-01-22T16:04:00"), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");
        Tap tapOn2 = createTap(3, LocalDateTime.parse("2023-01-22T16:00:00"), "ON", "Stop2", "Company1", "Bus37", "5500005555555559");

        Tap tapOn3 = createTap(5, LocalDateTime.parse("2023-01-22T17:00:00"), "ON", "Stop3", "Company1", "Bus37", "5500005555555559");

        when(fareRepository.getFare("Stop1", "Stop2")).thenReturn(new BigDecimal(3.25));
        when(fareRepository.getMaxFare("Stop3")).thenReturn(new BigDecimal(7.30));
        List<Tap> taps = List.of(tapOff1, tapOn1, tapOff2, tapOn2, tapOn3);

        List<Trip> allTrips = tripService.getTripsFromTaps(taps);
        Assertions.assertEquals(3, allTrips.size());

        Trip trip1 = allTrips.get(0);
        Assertions.assertEquals("Stop1", trip1.getFromStopId());
        Assertions.assertEquals("Stop2", trip1.getToStopId());
        Assertions.assertEquals(new BigDecimal(3.25), trip1.getChargeAmount());
        Assertions.assertEquals(300, trip1.getDurationSecs());
        Assertions.assertEquals(TripStatus.COMPLETED, trip1.getStatus());

        Trip trip2 = allTrips.get(1);
        Assertions.assertEquals("Stop2", trip2.getFromStopId());
        Assertions.assertEquals("Stop2", trip2.getToStopId());
        Assertions.assertEquals(BigDecimal.ZERO, trip2.getChargeAmount());
        Assertions.assertEquals(240, trip2.getDurationSecs());
        Assertions.assertEquals(TripStatus.CANCELLED, trip2.getStatus());

        Trip trip3 = allTrips.get(2);
        Assertions.assertEquals("Stop3", trip3.getFromStopId());
        Assertions.assertEquals("", trip3.getToStopId());
        Assertions.assertEquals(new BigDecimal(7.30), trip3.getChargeAmount());
        Assertions.assertEquals(0, trip3.getDurationSecs());
        Assertions.assertEquals(TripStatus.INCOMPLETE, trip3.getStatus());
    }

    private Tap createTap(int id, LocalDateTime dateTime, String tapType, String stopId, String companyId, String busId, String pan) {
        Tap tap = new Tap();
        tap.setId(id);
        tap.setDateTimeUTC(dateTime);
        tap.setTapType(tapType);
        tap.setStopId(stopId);
        tap.setCompanyId(companyId);
        tap.setBusId(busId);
        tap.setPan(pan);
        return tap;
    }
}
