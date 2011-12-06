package com.ag2.modelo;

import Grid.Entity;
import com.ag2.presentacion.Main;

public class ModeloCrearEnrutadorOptico extends ModeloCrearNodo{

    @Override
    public Entity crearNodoPhophorous(String nombreNodoGrafico) {
        System.out.println("Creo Enrutador Optico con monbre:"+nombreNodoGrafico);
        return Grid.Utilities.Util.createOCSSwitch(nombreNodoGrafico, Main.simulador, 10);
    }
}