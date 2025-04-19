package com.littlepay.faresystem.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Trip {
    private LocalDateTime started;
    private LocalDateTime finished;
    private long durationSecs;
    private String fromStopId;
    private String toStopId;
    private BigDecimal chargeAmount;
    private String companyId;
    private String busId;
    private String pan;
    private String status;
}
