package com.cattleDB.FrontEndService.controller;

import com.cattleDB.FrontEndService.config.CronJobConfig;
import com.cattleDB.FrontEndService.dtos.CronJobConfigWrapper;
import com.cattleDB.FrontEndService.models.Credentials;
import com.cattleDB.FrontEndService.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CronJobConfigControllerF {

    private final CronJobConfig cronJobConfig;
    private final CredentialsRepository credentialsRepository;

    @Autowired
    public CronJobConfigControllerF(CronJobConfig cronJobConfig, CredentialsRepository credentialsRepository) {
        this.cronJobConfig = cronJobConfig;
        this.credentialsRepository = credentialsRepository;
    }

    @GetMapping("/cron/config")
    public String getCronConfigPage(Model model) {
        CronJobConfigWrapper cronJobConfigWrapper = new CronJobConfigWrapper();
        cronJobConfigWrapper.setCronJobConfigs(cronJobConfig.currentCronJobConfig());
        model.addAttribute("cronJobConfigWrapper", cronJobConfigWrapper);

        // Fetch credentials and add to the model
        Optional<Credentials> credentials = credentialsRepository.findAll().stream().findFirst();
        model.addAttribute("credentials", credentials.orElse(new Credentials()));

        return "data-sync-settings";
    }

    @PostMapping("/cron/update")
    public String updateCronJobs(@ModelAttribute CronJobConfigWrapper cronJobConfigWrapper) {
        cronJobConfig.updateOrCreateCronJobs(cronJobConfigWrapper.getCronJobConfigs());
        return "redirect:/cron/config";  // Redirect back after updating
    }

    @PostMapping("/cron/update-cred")
    public String updateCredentials(@ModelAttribute Credentials updatedCredential, Model model) {
        // Delete all existing records
        credentialsRepository.deleteAll();

        // Save the new credential
        credentialsRepository.save(updatedCredential);

        model.addAttribute("message", "Credentials updated successfully");
        return "redirect:/cron/config";  // Redirect back after updating
    }
}
