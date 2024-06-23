package com.hatake.cattleDB.service;


import com.hatake.cattleDB.models.CsvEntity;
import com.hatake.cattleDB.repository.CsvRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private CsvRepository csvRepository;

    public Flux<String> processCsvFiles(List<MultipartFile> files) {
        return Flux.fromIterable(files)
                .flatMap(this::processCsv)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<String> processCsv(MultipartFile file) {
        return Flux.create(sink -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

                List<CsvEntity> csvEntities = new ArrayList<>();
                int count = 0;
                for (CSVRecord csvRecord : csvParser) {
                    CsvEntity csvEntity = new CsvEntity(
                            Boolean.parseBoolean(csvRecord.get("Valid")),
                            csvRecord.get("Time"),
                            csvRecord.get("Geofences"),
                            Double.parseDouble(csvRecord.get("Latitude")),
                            Double.parseDouble(csvRecord.get("Longitude")),
                            csvRecord.get("Altitude"),
                            csvRecord.get("Location Beacon"),0,0,
//                            Integer.parseInt(csvRecord.get("Major")),
//                            Integer.parseInt(csvRecord.get("Minor")),
                            csvRecord.get("UUID"),
                            csvRecord.get("Speed"),
                            csvRecord.get("Attributes"),
                            file.getOriginalFilename()
                    );
                    csvEntities.add(csvEntity);
                    count++;
                    sink.next("Processed line " + count + " from file " + file.getOriginalFilename());
                }

                csvRepository.saveAll(csvEntities);
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }


    public List<CsvEntity> getAllCsv(){
        return csvRepository.findAll();
    }
}










