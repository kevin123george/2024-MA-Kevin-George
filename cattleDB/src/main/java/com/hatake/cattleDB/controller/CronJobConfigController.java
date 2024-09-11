package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.CattleDbApplication;
import com.hatake.cattleDB.config.CronJobConfig;
import com.hatake.cattleDB.models.Beacon;
import com.hatake.cattleDB.models.CronJobConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cron")
//@CrossOrigin("http://localhost:5173/")
public class CronJobConfigController {

    @Autowired
    private CronJobConfig cronJobConfig;



    @PutMapping("/syncEventsCron")
    public void updateSyncEventsCron(@RequestParam String cronExpression) {
        cronJobConfig.setSyncEventsCron(cronExpression);
    }

    @PutMapping("/syncRoutesCron")
    public void updateSyncRoutesCron(@RequestParam String cronExpression) {
        cronJobConfig.setSyncRoutesCron(cronExpression);
    }

    @PutMapping("/syncSummaryCron")
    public void updateSyncSummaryCron(@RequestParam String cronExpression) {
        cronJobConfig.setSyncSummaryCron(cronExpression);
    }

    @PutMapping("/syncBeaconsCron")
    public void updateSyncBeaconsCron(@RequestParam String cronExpression) {
        cronJobConfig.setSyncBeaconsCron(cronExpression);
    }

    @PutMapping("/syncPositionsFixedRate")
    public void updateSyncPositionsFixedRate(@RequestParam long fixedRate) {
        cronJobConfig.setSyncPositionsFixedRate(fixedRate);

        CattleDbApplication.restart();
    }
    @GetMapping("/configs")
    public List<CronJobConfigEntity> getConfigs() {
        return  cronJobConfig.currentCronJobConfig();
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateCronJobs(@RequestBody List<CronJobConfigEntity> cronJobConfigs) {
        cronJobConfig.updateOrCreateCronJobs(cronJobConfigs);
        CattleDbApplication.restart();
        return ResponseEntity.ok("Cron job configurations updated/created successfully.");
    }
}