package com.littlepay.faresystem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CsvFareRepositoryTest {
    private static CsvFareRepository fareRepository;

    @BeforeAll
    static void setUp() {
        fareRepository = new CsvFareRepository("input/trip-fares-test.csv");
    }

    @Test
    void testFareBetweenTwoStops() {
        Assertions.assertEquals(new BigDecimal("3.25"), fareRepository.getFare("Stop1", "Stop2"));
        Assertions.assertEquals(new BigDecimal("5.50"), fareRepository.getFare("Stop2", "Stop3"));
        Assertions.assertEquals(new BigDecimal("7.30"), fareRepository.getFare("Stop1", "Stop3"));
    }

    @Test
    void testSymmetricFare() {
        Assertions.assertEquals(fareRepository.getFare("Stop1", "Stop2"), fareRepository.getFare("Stop2", "Stop1"));
        Assertions.assertEquals(fareRepository.getFare("Stop1", "Stop3"), fareRepository.getFare("Stop3", "Stop1"));
    }

    @Test
    void testFareWhenSourceAndDestinationAreSame() {
        Assertions.assertEquals(BigDecimal.ZERO, fareRepository.getFare("Stop1", "Stop1"));
        Assertions.assertEquals(BigDecimal.ZERO, fareRepository.getFare("Stop2", "Stop2"));
        Assertions.assertEquals(BigDecimal.ZERO, fareRepository.getFare("Stop3", "Stop3"));
    }

    @Test
    void testMaxFareForStop() {
        Assertions.assertEquals(new BigDecimal("7.30"), fareRepository.getMaxFare("Stop1"));
        Assertions.assertEquals(new BigDecimal("5.50"), fareRepository.getMaxFare("Stop2"));
        Assertions.assertEquals(new BigDecimal("7.30"), fareRepository.getMaxFare("Stop3"));
    }

    @Test
    void testExceptionForMissingFile() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            new CsvFareRepository("non_existing_file.csv");
        });

        Assertions.assertTrue(exception.getMessage().contains("Trip fares file not found"));
    }
}
