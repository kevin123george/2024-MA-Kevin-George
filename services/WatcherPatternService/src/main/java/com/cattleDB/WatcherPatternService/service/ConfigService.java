package com.cattleDB.WatcherPatternService.service;

import com.cattleDB.WatcherPatternService.models.AnalysisConfig;
import com.cattleDB.WatcherPatternService.repository.AnalysisConfigRepository;
import com.cattleDB.WatcherPatternService.repository.AnalysisTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    private AnalysisConfigRepository analysisConfigRepository;

    @Autowired
    private AnalysisTypeRepository analysisTypeRepository;

    public int getConfigValueAsInt(String analysisTypeName, String key, int defaultValue) {
        return analysisTypeRepository.findByName(analysisTypeName)
                .flatMap(type -> analysisConfigRepository.findByKeyAndAnalysisType(key, type))
                .map(config -> Integer.parseInt(config.getValue()))
                .orElse(defaultValue);
    }

    public double getConfigValueAsDouble(String analysisTypeName, String key, double defaultValue) {
        return analysisTypeRepository.findByName(analysisTypeName)
                .flatMap(type -> analysisConfigRepository.findByKeyAndAnalysisType(key, type))
                .map(config -> Double.parseDouble(config.getValue()))
                .orElse(defaultValue);
    }

    public String getConfigValueAsString(String analysisTypeName, String key, String defaultValue) {
        return analysisTypeRepository.findByName(analysisTypeName)
                .flatMap(type -> analysisConfigRepository.findByKeyAndAnalysisType(key, type))
                .map(AnalysisConfig::getValue)
                .orElse(defaultValue);
    }
}
