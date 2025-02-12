package com.cattleDb.DataInjectionService.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "credentials")
public class Credentials {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "safectory_api_cred")
    private String safectoryAPICred;
}
