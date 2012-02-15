package com.ag2.presentacion;

import com.ag2.config.PropertyPhosphorusTypeEnum;
import com.ag2.config.serializacion.UtilSerializator;
import com.ag2.controlador.*;
import com.ag2.modelo.*;
import com.ag2.presentacion.controles.GrupoDeDiseno;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

public class Main extends Application implements Serializable {

    private transient UtilSerializator serializador;
    private ControladorAdminNodo ctrlCreadorYAdministradorNodo;
    private ExecuteController executeAbstractController;
    private ModeloCrearNodo modeloCrearNodo;
    private LinkAdminAbstractController ctrlCrearYAdminEnlace;
    private GrupoDeDiseno grupoDeDiseno;
    private SimulacionBase simulacionBase =  SimulacionBase.getInstance(); 
    private ResultsController resultsController;
    

    @Override
    public void start(final Stage stage) {

        stage.setTitle("Modelo AG2- Simulador Grafico");       
        stage.setScene(IGU.getInstance());
        IGU.getInstance().setStage(stage);
        IGU.getInstance().setMain(this);
        stage.show();        
        grupoDeDiseno = IGU.getInstance().getGrGrupoDeDiseño();

        inicializarModelosYContrladoresDeCreacionDeNodos();
        IGU.getInstance().inicializarEstadoDeIGU();
        serializador = new UtilSerializator(this, stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) 
            {
                int result = JOptionPane.showConfirmDialog(
                null, "¿Desea guardar los cambios efectuados en la simulación?", "Simulador AG2", JOptionPane.YES_NO_CANCEL_OPTION);

                if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else if (result == JOptionPane.YES_OPTION) {
                    save(true);
                }event.consume();


            }
        });
        

    }
    

    public SimulacionBase getSimulacionBase() {
        return simulacionBase;
    }
   

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void inicializarModelosYContrladoresDeCreacionDeNodos() {

        //Controladores y Modelos
        ctrlCreadorYAdministradorNodo = new ControladorAdminNodo();
        executeAbstractController = new ExecuteController();
        resultsController = new ResultsController();

        IGU.getInstance().getGrGrupoDeDiseño().addControladorCrearNodo(ctrlCreadorYAdministradorNodo);
        
        ctrlCreadorYAdministradorNodo.addVistaGraficaNodoses(IGU.getInstance().getGrGrupoDeDiseño());
        ctrlCreadorYAdministradorNodo.addVistaGraficaNodoses(IGU.getInstance().getPropiedadesDispositivoTbl());
        IGU.getInstance().getPropiedadesDispositivoTbl().setControladorAbstractoAdminNodo(ctrlCreadorYAdministradorNodo);
        IGU.getInstance().getExecutePane().setExecuteAbstractController(executeAbstractController);


        modeloCrearNodo = new ModeloCrearCliente();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        modeloCrearNodo = new ModeloCrearNodoDeServicio();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        modeloCrearNodo = new ModeloCrearNodoDeRecurso();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        modeloCrearNodo = new ModeloCrearEnrutadorRafaga();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        modeloCrearNodo = new ModeloCrearEnrutadorOptico();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        modeloCrearNodo = new ModeloCrearEnrutadorHibrido();
        ctrlCreadorYAdministradorNodo.addModelo(modeloCrearNodo);

        ctrlCrearYAdminEnlace = new ControladorAdminEnlace();
        LinkCreationAbstractModel modeloCrearEnlace = new ModeloCrearEnlace();
        ctrlCrearYAdminEnlace.addModelo(modeloCrearEnlace);

        ctrlCrearYAdminEnlace.setVistaEnlace(IGU.getInstance().getPropiedadesDispositivoTbl());
        IGU.getInstance().getPropiedadesDispositivoTbl().setControladorAdminEnlace(ctrlCrearYAdminEnlace);
        IGU.getInstance().getGrGrupoDeDiseño().addControladorCrearEnlace(ctrlCrearYAdminEnlace);

       
        resultsController.setViewResultsPhosphorus(IGU.getInstance().getResustadosPhosphorus());
        SimulacionBase.getInstance().setResultsAbstractController(resultsController);
        
        IGU.getInstance().getTbvSimulationProperties().setItems(PropertyPhosphorusTypeEnum.getData(executeAbstractController));
       
    }

    public ResultsController getResultsController() {
        return resultsController;
    }

    public ControladorAdminNodo getCtrlCreadorYAdministradorNodo() {
        return ctrlCreadorYAdministradorNodo;
    }

    public LinkAdminAbstractController getCtrlCrearYAdminEnlace() {
        return ctrlCrearYAdminEnlace;
    }

    public ExecuteController getExecuteAbstractController() {
        return executeAbstractController;
    }

    public ModeloCrearNodo getModeloCrearNodo() {
        return modeloCrearNodo;
    }

    public UtilSerializator getSerializador() {
        return serializador;
    }

    public GrupoDeDiseno getGrupoDeDiseno() {
        return grupoDeDiseno;
    }
    public void loadFileBaseSimulation()
    {
        Main main = serializador.loadFileBaseSimulation();
        if (main != null) 
        {
            loadControllers(main);
        }
    }

    private void loadControllers(Main main) 
    {
        simulacionBase =   main.getSimulacionBase();  ///SimulacionBase.getInstance(); 
        SimulacionBase.loadInstance(simulacionBase); 
        grupoDeDiseno = main.getGrupoDeDiseno();
        
        IGU.getInstance().loadGrupoDeDiseno(grupoDeDiseno);
   
        ctrlCreadorYAdministradorNodo = main.getCtrlCreadorYAdministradorNodo();
        ctrlCrearYAdminEnlace = main.getCtrlCrearYAdminEnlace(); 
        executeAbstractController= main.getExecuteAbstractController();
        resultsController = main.getResultsController(); 
       
        modeloCrearNodo = main.getModeloCrearNodo(); 
        
        
        IGU.getInstance().getExecutePane().setExecuteAbstractController(executeAbstractController);
        resultsController.setViewResultsPhosphorus(IGU.getInstance().getResustadosPhosphorus());
        
        ctrlCrearYAdminEnlace.setVistaEnlace(IGU.getInstance().getPropiedadesDispositivoTbl());
        IGU.getInstance().getPropiedadesDispositivoTbl().setControladorAdminEnlace(ctrlCrearYAdminEnlace);
        
        SimulacionBase.getInstance().setControladorAdminEnlace(ctrlCrearYAdminEnlace);
        
        ctrlCreadorYAdministradorNodo.addVistaGraficaNodoses(IGU.getInstance().getGrGrupoDeDiseño());
        ctrlCreadorYAdministradorNodo.addVistaGraficaNodoses(IGU.getInstance().getPropiedadesDispositivoTbl());
        IGU.getInstance().getPropiedadesDispositivoTbl().setControladorAbstractoAdminNodo(ctrlCreadorYAdministradorNodo);
      //  executeAbstractController.stop();
    }

    public void save(boolean  thenClose) {
        serializador.OpenDialogToSave();
        if(thenClose)
        {
             System.exit(0);
        }
    }   

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException 
    {
        
        simulacionBase = SimulacionBase.getInstance(); 
        simulacionBase.getSimulador().getEntities(); 
        objectOutputStream.defaultWriteObject();

    }
    public void load() {
        Main main = serializador.OpenDialogToLoad();
        if (main != null) 
        {
            loadControllers(main);

        }
    }
}
