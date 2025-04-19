package com.littlepay.faresystem.service;

import com.littlepay.faresystem.model.Tap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TapOrganiserTest {
    @Test
    void shouldGroupAndSortTapsByPan() {
        Tap tap1 = createTap("5500005555555559", LocalDateTime.parse("2023-01-22T13:04:00"));
        Tap tap2 = createTap("4111111111111111", LocalDateTime.parse("2023-01-22T12:02:00"));
        Tap tap3 = createTap("5500005555555559", LocalDateTime.parse("2023-01-22T11:05:00"));

        List<Tap> taps = List.of(tap1, tap2, tap3);
        Map<String, List<Tap>> result = TapOrganiser.groupAndSortTapsByPan(taps);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.containsKey("5500005555555559"));
        Assertions.assertTrue(result.containsKey("4111111111111111"));

        List<Tap> pan1Taps = result.get("5500005555555559");
        Assertions.assertEquals(2, pan1Taps.size());
        Assertions.assertEquals(tap3, pan1Taps.get(0));
        Assertions.assertEquals(tap1, pan1Taps.get(1));

        List<Tap> pan2Taps = result.get("4111111111111111");
        Assertions.assertEquals(1, pan2Taps.size());
        Assertions.assertEquals(tap2, pan2Taps.get(0));
    }

    @Test
    void shouldReturnEmptyMapForEmptyInput() {
        Map<String, List<Tap>> result = TapOrganiser.groupAndSortTapsByPan(Collections.emptyList());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldSortTapsChronologicallyForEachPan() {
        Tap later = createTap("4111111111111111", LocalDateTime.parse("2023-01-22T13:00:00"));
        Tap earlier = createTap("4111111111111111", LocalDateTime.parse("2023-01-22T12:08:00"));

        Map<String, List<Tap>> result = TapOrganiser.groupAndSortTapsByPan(List.of(later, earlier));

        List<Tap> taps = result.get("4111111111111111");
        Assertions.assertEquals(2, taps.size());
        Assertions.assertEquals(earlier, taps.get(0));
        Assertions.assertEquals(later, taps.get(1));
    }

    private Tap createTap(String pan, LocalDateTime dateTime) {
        Tap tap = new Tap();
        tap.setPan(pan);
        tap.setDateTimeUTC(dateTime);
        return tap;
    }

}
