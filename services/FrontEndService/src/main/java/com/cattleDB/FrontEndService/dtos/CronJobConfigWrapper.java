package com.cattleDB.FrontEndService.dtos;


import com.cattleDB.FrontEndService.models.CronJobConfigEntity;

import java.util.ArrayList;
import java.util.List;

public class CronJobConfigWrapper {

    private List<CronJobConfigEntity> cronJobConfigs = new ArrayList<>();

    public List<CronJobConfigEntity> getCronJobConfigs() {
        return cronJobConfigs;
    }

    public void setCronJobConfigs(List<CronJobConfigEntity> cronJobConfigs) {
        this.cronJobConfigs = cronJobConfigs;
    }
}
