package com.capside.pokemondemo;

import static java.lang.Math.log;
import java.text.MessageFormat;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ciber
 */
@Service
@Log
public class TasksReportService {
    
    private final String mesosIP;
    private final String appName;
    private final String port;    
    private final String profile;

    @Autowired
    public TasksReportService(@Value("${mesos.ip}")String mesosIP, 
             @Value("${spring.application.name}") String appName, 
             @Value("${server.port}")String port, 
             Environment environment) {
        this.mesosIP = mesosIP;
        this.appName = appName;
        this.port = port;
        this.profile = environment.getActiveProfiles().length == 0 ? 
                       "" : environment.getActiveProfiles()[0];
    }

    public TaskReport generateTaskReport() {
        RestTemplate restTemplate = new RestTemplate();
        String mesosIP = "dev".equals(profile) ? "127.0.0.1" : this.mesosIP;
        log.info(MessageFormat.format("Obtaining report from {0}.", mesosIP));
        String reportUrl = "http://" + mesosIP + "/marathon/v2/apps/" + appName + "/tasks";
        TaskReport report = restTemplate.getForObject(reportUrl, TaskReport.class);
        for (Task task : report.getTasks()) {
            String host = "dev".equals(profile) ? "ciberadodcosagents.westeurope.cloudapp.azure.com" : task.getHost();
            String pokemonUrl = "http://" + host + ":" + port + "/";
            log.info(MessageFormat.format("Retreiving pokemon info from {0}.", host));
            Pokemon pokemon = restTemplate.getForObject(pokemonUrl, Pokemon.class);
            task.setPokemon(pokemon);
        }
        return report;
    }
    
    
    
}
