/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capside.pokemondemo;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ciber
 */
@Controller
@Scope(scopeName = "singleton")
@Log
public class PokemonCtrl {

    private final ConfigurableApplicationContext ctx;
    private final Pokemon pokemon;
    private final TasksReportService taskService;
    private final AttackService attackService;
    @Autowired
    public PokemonCtrl(ApplicationContext ctx, PokemonRepository repository, 
                       TasksReportService arenaService, AttackService attackService) {
        this.ctx = (ConfigurableApplicationContext) ctx;
        this.pokemon = repository.getRandomPokemon();
        this.taskService = arenaService;
        this.attackService = attackService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    String index(Map<String, Object> model) {
        String host = System.getenv("HOST") == null ? "dev" : System.getenv("HOST");
        model.put("host", host);
        model.put("pokemon", pokemon);
        
        Set<Map.Entry<String,String>> env = System.getenv().entrySet();
        model.put("env", env);
        model.put("task", System.getenv().get("MESOS_TASK_ID"));
        
        return "index"; 
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    Pokemon pokemon() {
        return pokemon;
    }    
    
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    @ResponseBody
    Pokemon shutdown() {
        log.warning(MessageFormat.format("{0} doesn''t want to fight, leaves the host now.", pokemon.getName()));
        new Thread(new Runnable() { 
            @Override @SneakyThrows
            public void run() { 
                Thread.sleep(1000);
                ctx.close(); 
            }
        }).start();
        return this.pokemon;
    }
    
    @RequestMapping(value = "/attack/{host:.+}", method = RequestMethod.POST)
    @ResponseBody
    void attack(@PathVariable String host) {
        log.warning(MessageFormat.format("I'm looking for trouble inside {0}.", host));
        attackService.sendDeleteToHost(host);
    }
    

    @RequestMapping(value="/arena", method = RequestMethod.GET)
    @ResponseBody 
    List<Task> arena() {
        TaskReport report = taskService.generateTaskReport();
        return report.getTasks();
    }
    
}
