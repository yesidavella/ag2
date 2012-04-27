package com.ag2.presentation;

import com.ag2.config.PropertyPhosphorusTypeEnum;
import com.ag2.config.serialization.UtilSerializator;
import com.ag2.controller.*;
import com.ag2.model.*;
import com.ag2.presentation.design.GraphDesignGroup;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;

public class Main extends Application implements Serializable {

    private transient UtilSerializator utilSerializator;
    private NodeAdminController nodeAdminController;
    private ExecuteController executeController;
    private NodeCreationModel nodeCreationModel;
    private LinkAdminAbstractController linkAdminAbstractController;
    private GraphDesignGroup graphDesignGroup;
    private SimulationBase simulationBase = SimulationBase.getInstance();
    private ResultsController resultsController;
    private JSObject browser;
    public static boolean IS_APPLET = false;
    private GUI iguAG2;

    @Override
    public void start(final Stage stage) {

        stage.setTitle("Simulador de infraestructura de grillas opticas AG2");
        
        try {
            browser = getHostServices().getWebContext();
            IS_APPLET = browser != null;
        } catch (Exception e) {
            IS_APPLET = false;
        }

        GUI.setStage(stage);
        
        iguAG2 = GUI.getInstance();
        
        stage.setScene(iguAG2);
        iguAG2.setStage(stage);
        iguAG2.setMain(this);
        stage.show();
        graphDesignGroup = iguAG2.getGraphDesignGroup();

        initModelsAndControllers();
        iguAG2.initStateGUI();

        utilSerializator = new UtilSerializator(this, stage);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                int result = JOptionPane.showConfirmDialog(
                        null, "¿Desea guardar los cambios efectuados en la simulación?", "Simulador AG2", JOptionPane.YES_NO_CANCEL_OPTION);

                if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else if (result == JOptionPane.YES_OPTION) {
                    save(true);
                }
                event.consume();
            }
        });
    }

    public SimulationBase getSimulationBase() {
        return simulationBase;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void initModelsAndControllers() {

        //Controladores y Modelos
        nodeAdminController = new NodeAdminController();
        executeController = new ExecuteController();
        resultsController = new ResultsController();

        iguAG2.getGraphDesignGroup().addNodeAdminAbstractControllers(nodeAdminController);

        nodeAdminController.addGraphNodesView(iguAG2.getGraphDesignGroup());
        nodeAdminController.addGraphNodesView(iguAG2.getEntityPropertyTb());
        iguAG2.getEntityPropertyTb().setControladorAbstractoAdminNodo(nodeAdminController);
        iguAG2.getExecutePane().setExecuteAbstractController(executeController);


        nodeCreationModel = new ClientCreationModel();
        nodeAdminController.addModel(nodeCreationModel);

        nodeCreationModel = new BrokerCreationModel();
        nodeAdminController.addModel(nodeCreationModel);

        nodeCreationModel = new ResourceCreationModel();
        nodeAdminController.addModel(nodeCreationModel);

        nodeCreationModel = new PCE_SwitchCreationModel();
        nodeAdminController.addModel(nodeCreationModel);

        nodeCreationModel = new HybridSwitchCreationModel();
        nodeAdminController.addModel(nodeCreationModel);

        linkAdminAbstractController = new LinkAdminController();
        LinkCreationAbstractModel modeloCrearEnlace = new LinkCreationModel();
        linkAdminAbstractController.addModel(modeloCrearEnlace);

        linkAdminAbstractController.setLinkView(iguAG2.getEntityPropertyTb());
        iguAG2.getEntityPropertyTb().setLinkAdminAbstractController(linkAdminAbstractController);
        iguAG2.getGraphDesignGroup().addLinkAdminAbstractControllers(linkAdminAbstractController);


        resultsController.setViewResultsPhosphorus(iguAG2.getPhosphosrusResults());
        SimulationBase.getInstance().setResultsAbstractController(resultsController);

        iguAG2.getTbwSimulationProperties().setItems(PropertyPhosphorusTypeEnum.getData(executeController));

    }

    public ResultsController getResultsController() {
        return resultsController;
    }

    public NodeAdminController getNodeAdminController() {
        return nodeAdminController;
    }

    public LinkAdminAbstractController getLinkAdminAbstractController() {
        return linkAdminAbstractController;
    }

    public ExecuteController getExecuteAbstractController() {
        return executeController;
    }

    public NodeCreationModel getNodeCreationModel() {
        return nodeCreationModel;
    }

    public UtilSerializator getUtilSerializator() {
        return utilSerializator;
    }

    public GraphDesignGroup getGraphDesignGroup() {
        return graphDesignGroup;
    }

    public void loadFileBaseSimulation() {
        Main main = utilSerializator.loadFileBaseSimulation();
        if (main != null) {
            loadControllers(main);
        }
    }

    private void loadControllers(Main main) {

        simulationBase = main.getSimulationBase();  ///SimulacionBase.getInstance();
        SimulationBase.loadInstance(simulationBase);
        graphDesignGroup = main.getGraphDesignGroup();

        iguAG2.loadGraphDesignGroup(graphDesignGroup);

        nodeAdminController = main.getNodeAdminController();
        linkAdminAbstractController = main.getLinkAdminAbstractController();
        executeController = main.getExecuteAbstractController();
        resultsController = main.getResultsController();

        nodeCreationModel = main.getNodeCreationModel();

        iguAG2.getExecutePane().setExecuteAbstractController(executeController);
        resultsController.setViewResultsPhosphorus(iguAG2.getPhosphosrusResults());

        linkAdminAbstractController.setLinkView(iguAG2.getEntityPropertyTb());
        iguAG2.getEntityPropertyTb().setLinkAdminAbstractController(linkAdminAbstractController);

        SimulationBase.getInstance().setLinkAdminAbstractController(linkAdminAbstractController);

        nodeAdminController.addGraphNodesView(iguAG2.getGraphDesignGroup());
        nodeAdminController.addGraphNodesView(iguAG2.getEntityPropertyTb());
        iguAG2.getEntityPropertyTb().setControladorAbstractoAdminNodo(nodeAdminController);
        //  executeAbstractController.stop();
    }

    public void save(boolean thenClose) {
        utilSerializator.OpenDialogToSave();
        if (thenClose) {
            System.exit(0);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        simulationBase = SimulationBase.getInstance();
        simulationBase.getGridSimulatorModel().getEntities();
        objectOutputStream.defaultWriteObject();
    }

    public void load() {
        Main main = utilSerializator.OpenDialogToLoad();
        if (main != null) {
            loadControllers(main);
        }
    }
}