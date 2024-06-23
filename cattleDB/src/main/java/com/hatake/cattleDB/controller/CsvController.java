package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.models.CsvEntity;
import com.hatake.cattleDB.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = "http://localhost:3000")
public class CsvController {

    @Autowired
    private CsvService csvService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> uploadCsv(@RequestPart("files") List<MultipartFile> files) {
        return csvService.processCsvFiles(files);
    }

    // @GetMapping
    // public long getAll() {
    //     List<CsvEntity> csvEntities = csvService.getAllCsv();
    //     System.out.println(csvEntities);
    //     return csvEntities.stream().count();
    // }


    @GetMapping("/cows")
    public HashSet<String> getCows() {
        var k = new HashSet<String>();
        csvService.getAllCsv().stream().forEach(d -> {
            k.add(d.getFilename());
        });
        return k;
    }


    @GetMapping()
    public ResponseEntity<List<CsvEntity>> getAllCsv() {
        return ResponseEntity.ok(csvService.getAllCsv());
    }
    @GetMapping("/download")
    public String testEndPoint() {
        var data =  csvService.getAllCsv().stream().findFirst();
        return data.get().getGeofences();
    }

}