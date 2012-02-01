/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ag2.presentacion;

import com.ag2.controlador.ExecuteAbstractController;
import com.ag2.presentacion.controles.Boton;
import com.ag2.presentacion.controles.GrupoDeDiseno;
import com.ag2.presentacion.controles.ResultadosPhosphorousHTML;
import com.ag2.presentacion.controles.ResultadosPhosphorus;
import java.io.Serializable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;

/**
 *
 * @author Frank
 */
public class ExecutePane extends TilePane implements ExecuteView, Serializable {

    private transient Boton btnEjecutar;
    private transient Boton btnParar;
    private transient ToggleGroup tgEjecucion = new ToggleGroup();
    private transient ResultadosPhosphorousHTML resultadosPhosphorousHTML;
    ResultadosPhosphorus resultadosPhosphorus; 
    ExecuteAbstractController executeAbstractController;

    public void setExecuteAbstractController(ExecuteAbstractController executeAbstractController) {
        this.executeAbstractController = executeAbstractController;
        executeAbstractController.setExecuteView(this);
    }

    public void setResultadosPhosphorousHTML(ResultadosPhosphorousHTML resultadosPhosphorousHTML) {
        this.resultadosPhosphorousHTML = resultadosPhosphorousHTML;
    }

    public void setResultadosPhosphorus(ResultadosPhosphorus resultadosPhosphorus) {
        this.resultadosPhosphorus = resultadosPhosphorus;
    }
    
     public void habilitar()
    {
        btnParar.setSelected(true);    
        //IGU.setEstadoTipoBoton(TiposDeBoton.PARAR);                       
                      
    }

    public ExecutePane(Group group) {

        btnEjecutar = new Boton(TiposDeBoton.EJECUTAR) {

            @Override
            public void setGrupoDeDiseño(final Group grGrupoDeDiseño) {
                setOnMouseClicked(new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent mouEvent) {
                        
                        Boton btnEjecutar = (Boton)mouEvent.getSource();
                        
                        btnEjecutar.setSelected(true);

                        IGU.getInstance().deshabilitar();
                        //IGU.setEstadoTipoBoton(TiposDeBoton.EJECUTAR);
                        
                        if (executeAbstractController != null)
                        {
                            if (resultadosPhosphorousHTML != null)
                            {
                                resultadosPhosphorousHTML.lookToNextExecution();
                            }
                            if(resultadosPhosphorus!=null)
                            {
                                resultadosPhosphorus.looktToNextExecution();
                            }
                            
                            if(executeAbstractController.isWellFormedNetwork()){
                                executeAbstractController.run();
                            }else{
                                //Muestro la ventana con la joda mal
                            }
                            

                        }
                    }
                });
            }
        };

        btnParar = new Boton(TiposDeBoton.PARAR) {

            @Override
            public void setGrupoDeDiseño(final Group grGrupoDeDiseño) {
                setOnMouseClicked(new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent mouEvent) {
                        
                        Boton btnParar = (Boton)mouEvent.getSource();
                        IGU.getInstance().habilitar();                        
                        habilitar();
                        executeAbstractController.stop();
                    }
                });
            }
        };
        
       


        getStyleClass().add("barraDeHerramientas");
        setPadding(new Insets(10, 10, 10, 10));
        setHgap(4);
        setPrefColumns(2);

        Tooltip tTipBtnEjecutar = new Tooltip("Ejecutar simulación");
        btnEjecutar.setTooltip(tTipBtnEjecutar);
        btnEjecutar.setToggleGroup(tgEjecucion);




        btnEjecutar.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent t) {
            }
        });



        Tooltip tTipBtnParar = new Tooltip("Parar simulación");

        btnParar.setTooltip(tTipBtnParar);
        btnParar.setToggleGroup(tgEjecucion);
        btnParar.setSelected(true);
        getChildren().addAll(btnEjecutar, btnParar);

        btnEjecutar.setGrupoDeDiseño(group);
        btnParar.setGrupoDeDiseño(group);
    }
}
