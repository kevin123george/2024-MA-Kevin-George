package com.hatake.cattleDB.dtos;

import lombok.Data;

import java.util.List;

@Data
public class RouteRequest {
    private List<Integer> deviceIds;
    private List<String> trackableIds;
    private List<String> types;
    private String from;
    private String to;
    private boolean filterSameLocation;
    private boolean filterFlapping;
    private boolean filterNonBeacon;
    private int gapTime;
    private int filterDurationMin;
    private String queryId;
}
