
package com.ag2.presentation.design.property;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;


public class PropiedadNodoDistribuciones extends EntityProperty {

    public enum TipoDeDistribucion {

        CONSTANT("Constante"),
        ER_LANG("Erlang"),
        HYPER_EXPONENTIAL("Hiper-exponencial"),
        NEGATIVE_EXPONENTIAL("Exponencial-negativa"),
        NORMAL("Normal"),
        POISSON_PROCESS("Possion"),
        UNMIFORM("Uniforme");
        private String nombre;


        private TipoDeDistribucion(String nombre) {
            this.nombre = nombre;

        }

        @Override
        public String toString() {
            return nombre.toString();
        }
    };

    public PropiedadNodoDistribuciones(String id, String nombre) {
        super(id, nombre, PropertyType.TEXT_LIST, false );
        ((ChoiceBox) control).setItems(FXCollections.observableArrayList(TipoDeDistribucion.values()));
    }

    public void setPrimerValor(TipoDeDistribucion tipoDeDistribucion) {
        ((ChoiceBox) control).getSelectionModel().select(tipoDeDistribucion);
    }
}
