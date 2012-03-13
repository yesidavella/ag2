package com.ag2.config;

import com.ag2.controller.ExecuteController;
import com.ag2.presentation.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * Carga el archivo .cfg con el valor de las constantes de la simulación.
 *
 * @author Yesid :D
 */
public enum PropertyPhosphorusTypeEnum {

    /**
     * Tiempo de simulacion en milisegundos.
     */
    SIMULATION_TIME("simulationTime", "Tiempo de Simulación(ms):", new TextField()),
    /**
     * Se desea que genere o no archivos HTML del paso a paso de la simulacion.
     */
    OUTPUT("output", "output(Franklin)", new CheckBox()),
    //STOP_EVENT_OFF_SETTIME("stopEventOffSetTime","stopEventOffSetTime", new TextField()),
    SWITCHING_SPEED("switchingSpeed", "Vel. de conmutación:", new TextField()),
    /**
     * Cantidad de lambdas en la fibra optica.
     */
    DEFAULT_WAVELENGTHS("defaultWavelengths", "Numero de lambdas:", new TextField()),
    //The size of control messages. If control messages are 0, the are being send immediately
    ACK_SIZE("ACKsize", "ACKsize franklin:", new TextField()),
    /**
     * Tiempo de retardo al conmutar un msg, solo usado en OBSSwitchImpl, NO en
     * conmutadores hibridos. Esto es la propiedad HandleDelay.
     */
    //OBS_HANDLE_TIME("OBSHandleTime", "Tiempo de retardo al conmutar:", new TextField()),
    DEFAULT_CPU_CAPACITY("defaultCpuCapacity", "Capacidad de CPUs/clúster:", new TextField()),//Antes se llamaba DEFAULT_CAPACITY
    DEFAULT_CPU_COUNT("defaultCPUCount", "Número de CPUs/clúster:", new TextField()),
    /**
     * Cantidad de trabajos a encolar en el buffer del nodo recurso
     */
    DEFAULT_QUEUE_SIZE("defaultQueueSize", "Buffer de Trabajos/cluster:", new TextField()),
    DEFAULT_FLOP_SIZE("defaultFlopSize", "a", new TextField()),
    DEFAULT_DATA_SIZE("defaultDataSize", "a", new TextField()),
    DEFAULT_JOB_IAT("defaultJobIAT", "a", new TextField()),
    /**
     * Promedio de la exp. neg. del retraso maximo en la solicitud del trabajo
     * Al parecer nunca se utiliza. Yo diria q no esta implementado.
     */
    MAX_DELAY("maxDelay", "Prom. Retraso máx/Trab_Req:", new TextField()),
    OUTPUT_FILE_NAME("outputFileName", "Nombre del archivo de traza:", new TextField()),
    /**
     * Tiempo q se demora en crear o eliminar un OCS. Solo usado en el OCSEndSender. 
     */
    OCS_SETUP_HANDLE_TIME("OCSSetupHandleTime", "Tiempo crear/eliminar un OCS(ms):", new TextField()),
    /**
     * Tiempo necesario en alcanzar la otra punta del enlace de un mensaje.
     */
    LINK_SPEED("linkSpeed", "Vel. del enlace(ms):", new TextField()),
    //DEFAULT_LINK_SPEED("defaultLinkSpeed", "a", new TextField()),
    ROUTED_VIA_JUNG("routedViaJUNG", "Enrutar via:(T)Jung (F)ShortesPath", new CheckBox());
    private ExecuteController executeController;
    private String phosphorusPropertyName;
    private Control control;
    private PhosphorusPropertyEditor phosphorusPropertyEditor = PhosphorusPropertyEditor.getUniqueInstance();
    private String visualNameOnTb;

    public String getVisualNameOnTb() {
        return visualNameOnTb;
    }

    private PropertyPhosphorusTypeEnum(String nombre, String visualNameOnTb, Control control) {
        this.visualNameOnTb = visualNameOnTb;
        phosphorusPropertyName = nombre;
        this.control = control;
        if (control instanceof TextField) {
            setPropertyEvent((TextField) control);

        } else if (control instanceof CheckBox) {
            setEventProperty(((CheckBox) control));
        }
    }

    public void setExecuteController(ExecuteController executeController) {
        this.executeController = executeController;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public String getPropertyName() {
        return this.toString().replace("_", " ");
    }

    private void setEventProperty(CheckBox checkBox) {

        if (phosphorusPropertyEditor.getPropertyValue(this).equalsIgnoreCase("true")) {
            checkBox.setSelected(true);
        }
        checkBox.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent actionEvent) {
                CheckBox checkBox = (CheckBox) actionEvent.getSource();
                if (checkBox.isSelected()) {
                    PropertyPhosphorusTypeEnum.this.writeProperty(Boolean.TRUE.toString());
                } else {
                    PropertyPhosphorusTypeEnum.this.writeProperty(Boolean.FALSE.toString());
                }
            }
        });
    }

    private void setPropertyEvent(final TextField textField) {

        textField.setText(phosphorusPropertyEditor.getPropertyValue(this));

        textField.setOnMouseExited(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                GUI.getInstance().getGraphDesignGroup().getGroup().requestFocus();
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

            public void changed(ObservableValue<? extends Boolean> textControl, Boolean beforeStateFocus, Boolean currentStateFocus) {

                if (beforeStateFocus == true && currentStateFocus == false) {
                    String value = textField.getText();
                    PropertyPhosphorusTypeEnum.this.writeProperty(value);
                    executeController.reLoadConfigFile();
                }
            }
        });
    }

    public String getPhosphorusPropertyName() {
        return phosphorusPropertyName;
    }

    public void writeProperty(String valor) {
        phosphorusPropertyEditor.setPropertyValue(this, valor);
    }

    public static ObservableList getData(ExecuteController executeController) {
        for (PropertyPhosphorusTypeEnum propertyPhosphorusTypeEnum : values()) {
            propertyPhosphorusTypeEnum.setExecuteController(executeController);
        }

        ObservableList observableList = FXCollections.observableArrayList(PropertyPhosphorusTypeEnum.values());
        return observableList;
    }

    public static double getDoubleProperty(PropertyPhosphorusTypeEnum key) {
        PhosphorusPropertyEditor phosPropEditor = PhosphorusPropertyEditor.getUniqueInstance();
        String propertie = phosPropEditor.getProperties().getProperty(key.getPhosphorusPropertyName());
        if (propertie == null) {
            throw new IllegalArgumentException(key.toString() + " no esta en el archivo de configuración.");
        }
        return Double.parseDouble(propertie);
    }

    public static boolean getBooleanProperty(PropertyPhosphorusTypeEnum key) {
        PhosphorusPropertyEditor phosPropEditor = PhosphorusPropertyEditor.getUniqueInstance();
        String propertie = phosPropEditor.getProperties().getProperty(key.getPhosphorusPropertyName());
        return Boolean.parseBoolean(propertie);
    }

    public static long getLongProperty(PropertyPhosphorusTypeEnum key) {
        PhosphorusPropertyEditor phosPropEditor = PhosphorusPropertyEditor.getUniqueInstance();
        String propertie = phosPropEditor.getProperties().getProperty(key.getPhosphorusPropertyName());
        return Long.parseLong(propertie);
    }

    public static int getIntProperty(PropertyPhosphorusTypeEnum key) {
        PhosphorusPropertyEditor phosPropEditor = PhosphorusPropertyEditor.getUniqueInstance();
        String propertie = phosPropEditor.getProperties().getProperty(key.getPhosphorusPropertyName());
        return Integer.parseInt(propertie);
    }
}