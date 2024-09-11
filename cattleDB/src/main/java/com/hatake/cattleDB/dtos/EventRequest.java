package com.hatake.cattleDB.dtos;

import lombok.Data;

import java.util.List;


@Data
public class EventRequest {
    private List<Integer> deviceId;
    private List<Integer> trackableId;
    private List<String> type;
    private String from;
    private String to;
    private boolean filterSameLocation;
    private boolean filterFlapping;
    private boolean filterNonBeacon;
    private int gapTime;
    private int filterDurationMin;
    private Object queryId;
}