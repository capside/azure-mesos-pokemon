package com.capside.pokemondemo;

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
public class AttackService {

    private final String port;    
    private final String profile;

    @Autowired
    public AttackService(@Value("${server.port}")String port, Environment environment) {
        this.port = port;
        this.profile = environment.getActiveProfiles().length == 0 ? 
                       "" : environment.getActiveProfiles()[0];
    }
    
    public void sendDeleteToHost(String host) {
        RestTemplate template = new RestTemplate();
        host = "dev".equals(profile) ? "ciberadodcosagents.westeurope.cloudapp.azure.com" : host;
        log.info(MessageFormat.format("Attacking {0}.", host));
        String url = "http://" + host + ":" + port + "/";
        template.delete(url);
    }
    
}
