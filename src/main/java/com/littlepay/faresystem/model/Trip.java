package com.littlepay.faresystem.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Trip {
    private LocalDateTime started;
    private LocalDateTime finished;
    private int durationSecs;
    private String fromStopId;
    private String toStopId;
    private double chargeAmount;
    private String companyId;
    private String busId;
    private String pan;
    private String status;
}
