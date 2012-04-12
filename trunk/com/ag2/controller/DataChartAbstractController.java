/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ag2.controller;

import com.ag2.presentation.design.GraphNode;

/**
 *
 * @author Frank
 */
public abstract class DataChartAbstractController 
{
    
    protected double time; 
    protected double value1; 
    protected double value2; 
    
    public abstract void loadDataChartResourceCPU(GraphNode graphNode);
    public abstract void loadDataChartResourceBuffer(GraphNode graphNode);

    public double getTime() {
        return time;
    }

    public double getValue1() {
        return value1;
    }

    public double getValue2() {
        return value2;
    }
    
      
    
    
}
