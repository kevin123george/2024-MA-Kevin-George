package com.namikaze.hatake.controller;

import com.namikaze.hatake.models.CsvEntity;
import com.namikaze.hatake.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvService csvService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> uploadCsv(@RequestPart("files") List<MultipartFile> files) {
        return csvService.processCsvFiles(files);
    }

    @GetMapping
    public ResponseEntity<List<CsvEntity>> getAll() {
        List<CsvEntity> csvEntities = csvService.getAllCsv();
        return ResponseEntity.ok(csvEntities);
    }

}