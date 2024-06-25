package com.hatake.cattleDB.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SummaryRequest {
    private List<Integer> deviceId;
    private List<String> trackableId;
    private List<String> type;
    private String from;
    private String to;
    private boolean filterSameLocation;
    private boolean filterFlapping;
    private boolean filterNonBeacon;
    private int gapTime;
    private int filterDurationMin;
    private String queryId;
}