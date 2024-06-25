//package com.hatake.cattleDB.models;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter
//@Setter
//@ToString
//@RequiredArgsConstructor
//@Table(name = "route_attributes")
//public class RouteAttributes {
//
//    @Id
//    private Long id;
//
//    private Double batteryLevel;
//    private Double clickcount;
//    private Double activationLevel;
//    private Double scanRssi;
//    private String appid;
//
//    @OneToOne(mappedBy = "attributes", cascade = CascadeType.ALL)
//    private RouteEntity routeEntity;
//    }