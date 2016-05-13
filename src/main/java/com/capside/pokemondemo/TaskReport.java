/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capside.pokemondemo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ciber
 */
@Data
public class TaskReport {
    
    private List<Task> tasks = new ArrayList<>();
    
}
