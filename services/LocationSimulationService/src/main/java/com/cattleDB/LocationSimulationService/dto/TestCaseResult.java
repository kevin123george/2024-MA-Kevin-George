package com.cattleDB.LocationSimulationService.dto;


import lombok.Data;

@Data
public class TestCaseResult {
    public int testCase;
    public long totalLocations;
    public long distinctEntities;
    public long coveredSpaces;
    public String mostVisitedSpace;
    public long totalSpacesMoved;
    public double avgSpacesMoved;
    public double avgDistanceMovedInKm;
}
