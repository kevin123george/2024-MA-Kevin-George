package com.cattleDb.DataInjectionService.config;

import com.cattleDb.DataInjectionService.models.CronJobConfigEntity;
import com.cattleDb.DataInjectionService.repository.CronJobConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CronJobConfig {

    @Autowired
    private CronJobConfigRepository cronJobConfigRepository;

    private String syncEventsCron = "0 */10 * * * *"; // Default value
    private String syncRoutesCron = "0 0 */3 * * *";  // Default value
    private String syncSummaryCron = "0 */10 * * * *"; // Default value
    private String syncBeaconsCron = "0 */1 * * * *";  // Default value
    private long syncPositionsFixedRate = 2000L;      // Default value

    @PostConstruct
    public void loadCronConfigs() {
        // Load from the database if exists, else use default values
        loadCronConfigFromDatabase("syncEvents", this::setSyncEventsCron);
        loadCronConfigFromDatabase("syncRoutes", this::setSyncRoutesCron);
        loadCronConfigFromDatabase("syncSummary", this::setSyncSummaryCron);
        loadCronConfigFromDatabase("syncBeacons", this::setSyncBeaconsCron);
        loadFixedRateFromDatabase("syncPositions", this::setSyncPositionsFixedRate);
    }

    private void loadCronConfigFromDatabase(String jobName, java.util.function.Consumer<String> setter) {
        CronJobConfigEntity config = cronJobConfigRepository.findByJobName(jobName);
        if (config != null && config.getCronExpression() != null) {
            setter.accept(config.getCronExpression());
        }
    }

    private void loadFixedRateFromDatabase(String jobName, java.util.function.Consumer<Long> setter) {
        CronJobConfigEntity config = cronJobConfigRepository.findByJobName(jobName);
        if (config != null && config.getFixedRate() != null) {
            setter.accept(config.getFixedRate());
        }
    }

    public List<CronJobConfigEntity> currentCronJobConfig() {
        return cronJobConfigRepository.findAll();
    }

    public String getSyncEventsCron() {
        return syncEventsCron;
    }

    public void setSyncEventsCron(String syncEventsCron) {
        this.syncEventsCron = syncEventsCron;
        saveCronConfig("syncEvents", syncEventsCron, null);
    }

    public String getSyncRoutesCron() {
        return syncRoutesCron;
    }

    public void setSyncRoutesCron(String syncRoutesCron) {
        this.syncRoutesCron = syncRoutesCron;
        saveCronConfig("syncRoutes", syncRoutesCron, null);
    }

    public String getSyncSummaryCron() {
        return syncSummaryCron;
    }

    public void setSyncSummaryCron(String syncSummaryCron) {
        this.syncSummaryCron = syncSummaryCron;
        saveCronConfig("syncSummary", syncSummaryCron, null);
    }

    public String getSyncBeaconsCron() {
        return syncBeaconsCron;
    }

    public void setSyncBeaconsCron(String syncBeaconsCron) {
        this.syncBeaconsCron = syncBeaconsCron;
        saveCronConfig("syncBeacons", syncBeaconsCron, null);
    }

    public long getSyncPositionsFixedRate() {
        return syncPositionsFixedRate;
    }

    public void setSyncPositionsFixedRate(long syncPositionsFixedRate) {
        this.syncPositionsFixedRate = syncPositionsFixedRate;
        saveCronConfig("syncPositions", null, syncPositionsFixedRate);
    }

    private void saveCronConfig(String jobName, String cronExpression, Long fixedRate) {
        CronJobConfigEntity cronJobConfigEntity = cronJobConfigRepository.findByJobName(jobName);
        if (cronJobConfigEntity == null) {
            cronJobConfigEntity = new CronJobConfigEntity();
            cronJobConfigEntity.setJobName(jobName);
        }
        if (cronExpression != null) {
            cronJobConfigEntity.setCronExpression(cronExpression);
        }
        if (fixedRate != null) {
            cronJobConfigEntity.setFixedRate(fixedRate);
        }
        cronJobConfigRepository.save(cronJobConfigEntity);
    }


    public void updateOrCreateCronJobs(List<CronJobConfigEntity> cronJobConfigs) {
        for (CronJobConfigEntity config : cronJobConfigs) {
            CronJobConfigEntity existingConfig = cronJobConfigRepository.findById(config.getJobName()).orElse(null);

            if (existingConfig != null) {
                // Update existing record
                existingConfig.setCronExpression(config.getCronExpression());
                existingConfig.setFixedRate(config.getFixedRate());
                existingConfig.setEnabled(config.isEnabled());
                cronJobConfigRepository.save(existingConfig);
            } else {
                // Create new record
                cronJobConfigRepository.save(config);
            }
        }
    }


    public List<CronJobConfigEntity> saveAll(List<CronJobConfigEntity> cronJobConfigs) {
        return cronJobConfigRepository.saveAll(cronJobConfigs);
    }
}
