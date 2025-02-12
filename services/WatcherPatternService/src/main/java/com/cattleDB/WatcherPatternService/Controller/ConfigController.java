package com.cattleDB.WatcherPatternService.Controller;

import com.cattleDB.WatcherPatternService.models.AnalysisConfig;
import com.cattleDB.WatcherPatternService.models.AnalysisType;
import com.cattleDB.WatcherPatternService.repository.AnalysisConfigRepository;
import com.cattleDB.WatcherPatternService.repository.AnalysisTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/configs")
@CrossOrigin("*")
public class ConfigController {

    @Autowired
    private AnalysisConfigRepository analysisConfigRepository;

    @Autowired
    private AnalysisTypeRepository analysisTypeRepository;

    // Create a new configuration
    @PostMapping
    public ResponseEntity<AnalysisConfig> createConfig(@RequestBody AnalysisConfig config) {
        Optional<AnalysisType> analysisType = analysisTypeRepository.findByName(config.getKey());
        if (analysisType.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        config.setAnalysisType(analysisType.get());
        AnalysisConfig savedConfig = analysisConfigRepository.save(config);
        return ResponseEntity.ok(savedConfig);
    }

    // Get all configurations
    @GetMapping
    public List<AnalysisConfig> getAllConfigs() {
        List<AnalysisConfig> configs = analysisConfigRepository.findAll();
        return (configs);
    }

    // Get a configuration by ID
    @GetMapping("/{id}")
    public Optional<AnalysisConfig> getConfigById(@PathVariable Long id) {
        Optional<AnalysisConfig> config = analysisConfigRepository.findById(id);
        return config;
    }

    // Update a configuration
    @PutMapping("/{id}")
    public AnalysisConfig updateConfig(@PathVariable("id") Long id, @RequestBody AnalysisConfig updatedConfig) {
        Optional<AnalysisConfig> existingConfig = analysisConfigRepository.findById(id);
        if (existingConfig.isEmpty()) {
            return new AnalysisConfig();
        }

        AnalysisConfig config = existingConfig.get();
        Optional<AnalysisType> analysisType = analysisTypeRepository.findByName(updatedConfig.getKey());
        if (analysisType.isEmpty()) {
            return new AnalysisConfig();
        }

        config.setKey(updatedConfig.getKey());
        config.setValue(updatedConfig.getValue());
        config.setDataType(updatedConfig.getDataType());
        config.setDescription(updatedConfig.getDescription());
        config.setAnalysisType(analysisType.get());

        AnalysisConfig savedConfig = analysisConfigRepository.save(config);
        return savedConfig;
    }

    // Delete a configuration
    @DeleteMapping("/{id}")
    public void deleteConfig(@PathVariable Long id) {
        Optional<AnalysisConfig> config = analysisConfigRepository.findById(id);
        analysisConfigRepository.deleteById(id);
    }
}
