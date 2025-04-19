package com.littlepay.faresystem.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Tap {
    private int id;
    private LocalDateTime dateTimeUTC;
    private String tapType;
    private String tapId;
    private String companyId;
    private String busId;
    private String pan;
}
