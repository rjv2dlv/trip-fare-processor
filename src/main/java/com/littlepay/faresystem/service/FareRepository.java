package com.littlepay.faresystem.service;

import java.math.BigDecimal;

public interface FareRepository {
    BigDecimal getFare(String sourceStop, String destinationStop);

    BigDecimal getMaxFare(String stop);
}
