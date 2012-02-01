/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ag2.controlador;

import com.ag2.modelo.SimulacionBase;

/**
 *
 * @author Frank
 */
public class ExecuteController extends  ExecuteAbstractController
{  
    @Override
    public void run()
    {
        
       Thread thread = new Thread(SimulacionBase.getInstance());
       thread.start();
       System.out.println("###########----  RUN ----####################################################");
       
    }

    @Override
    public void stop() {
        SimulacionBase.getInstance().stop();
    }

    @Override
    public boolean isWellFormedNetwork() {
        /*
         * 
         */
        //NetworkChecker.getInstance.passCheck() true o false.
        return true;
    }
    
}
