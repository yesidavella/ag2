package com.ag2.controlador;

import Grid.Entity;
import Grid.Port.GridOutPort;
import com.ag2.modelo.EnlacePhosphorous;
import com.ag2.modelo.ModeloAbstractoCrearEnlace;
import com.ag2.modelo.ModeloCrearEnlace;
import com.ag2.presentacion.diseño.EnlaceGrafico;
import com.ag2.presentacion.diseño.NodoGrafico;
import com.ag2.presentacion.diseño.propiedades.PropiedadeNodo;
import com.ag2.util.ContenedorParejasObjetosExistentes;
import java.util.ArrayList;

public class ControladorAdminEnlace extends ControladorAbstractoAdminEnlace {

    private Entity nodoPhosphorousA;
    private Entity nodoPhosphorousB; 
    private GridOutPort puertoSalidaNodoA;
    private GridOutPort puertoSalidaNodoB;

    @Override
    public void crearEnlace(EnlaceGrafico enlaceGrafico) {
                
        for(ModeloAbstractoCrearEnlace modelo:modelosRegistrados){

            if(modelo instanceof ModeloCrearEnlace){
                
                NodoGrafico nodoGraficoA = enlaceGrafico.getNodoGraficoA();
                NodoGrafico nodoGraficoB = enlaceGrafico.getNodoGraficoB();

                nodoPhosphorousA = (Entity) ContenedorParejasObjetosExistentes.getInstanciaParejasDeNodosExistentes().get(nodoGraficoA);
                nodoPhosphorousB = (Entity) ContenedorParejasObjetosExistentes.getInstanciaParejasDeNodosExistentes().get(nodoGraficoB);
                
                if( nodoPhosphorousA!=null && nodoPhosphorousB!=null){
                    EnlacePhosphorous nuevoEnlacePhosphorous = modelo.crearEnlacePhosphorous(nodoPhosphorousA,nodoPhosphorousB);
                    ContenedorParejasObjetosExistentes.getInstanciaParejasDeEnlacesExistentes().put(enlaceGrafico, nuevoEnlacePhosphorous);
                }else{
                    System.out.println("Algun nodo PHOSPHOROUS esta NULL, NO se creo el ENLACE en PHOPHOROUS.");
                }
            }
        }
    }

    @Override
    public void consultarPropiedades(EnlaceGrafico enlaceGrafico) {

        ArrayList<PropiedadeNodo> propiedadesDeEnlace = new ArrayList<PropiedadeNodo>();
        EnlacePhosphorous enlacePhosSeleccionado = (EnlacePhosphorous)ContenedorParejasObjetosExistentes.getInstanciaParejasDeEnlacesExistentes().get(enlaceGrafico);
        puertoSalidaNodoA = enlacePhosSeleccionado.getPuertoSalidaNodoPhosA();
        puertoSalidaNodoB = enlacePhosSeleccionado.getPuertoSalidaNodoPhosB();
        /*
         **Enlace de nodo Phosphorous de A hacia B (A->B)
         */
        //===========================================================================================================
        PropiedadeNodo propNombreDireccionCanalAB = new PropiedadeNodo("direcciónCanalAB", "Dirección del Canal:", PropiedadeNodo.TipoDePropiedadNodo.ETIQUETA);
        propNombreDireccionCanalAB.setPrimerValor(enlaceGrafico.getNodoGraficoA().getNombre()+"-->"+enlaceGrafico.getNodoGraficoB().getNombre());
        propiedadesDeEnlace.add(propNombreDireccionCanalAB);
        
        PropiedadeNodo propVelEnlaceAB = new PropiedadeNodo("linkSpeedAB", "Vel. del Enlace:", PropiedadeNodo.TipoDePropiedadNodo.NUMERO);
        propVelEnlaceAB.setPrimerValor(Double.toString(puertoSalidaNodoA.getLinkSpeed()));
        propiedadesDeEnlace.add(propVelEnlaceAB);
        //===========================================================================================================

        /*
         **Enlace de nodo Phosphorous de B hacia A (B->A)
         */
        //===========================================================================================================
        PropiedadeNodo propNombreDireccionCanalBA = new PropiedadeNodo("direcciónCanalBA", "Dirección del Canal:", PropiedadeNodo.TipoDePropiedadNodo.ETIQUETA);
        propNombreDireccionCanalBA.setPrimerValor(enlaceGrafico.getNodoGraficoB().getNombre()+"-->"+enlaceGrafico.getNodoGraficoA().getNombre());
        propiedadesDeEnlace.add(propNombreDireccionCanalBA);
        
        PropiedadeNodo propVelEnlaceBA = new PropiedadeNodo("linkSpeedBA", "Vel. del Enlace:", PropiedadeNodo.TipoDePropiedadNodo.NUMERO);
        propVelEnlaceBA.setPrimerValor(Double.toString(puertoSalidaNodoB.getLinkSpeed()));
        propiedadesDeEnlace.add(propVelEnlaceBA);
        //===========================================================================================================
        
        /*
         **Propiedades comunes en ambas direcciones del canal.
         */
        PropiedadeNodo propVelConmutacion = new PropiedadeNodo("switchingSpeed", "Vel. de Conmutación:", PropiedadeNodo.TipoDePropiedadNodo.NUMERO);
        propVelConmutacion.setPrimerValor( (puertoSalidaNodoA.getSwitchingSpeed()==puertoSalidaNodoB.getSwitchingSpeed())?Double.toString(puertoSalidaNodoB.getSwitchingSpeed()):"Problema leyendo Vel. de conmutación.");
        propiedadesDeEnlace.add(propVelConmutacion);
        
        PropiedadeNodo propWavelengths = new PropiedadeNodo("defaultWavelengths", "Cantidad de λs:", PropiedadeNodo.TipoDePropiedadNodo.NUMERO);
        propWavelengths.setPrimerValor( (puertoSalidaNodoA.getMaxNumberOfWavelengths()==puertoSalidaNodoB.getMaxNumberOfWavelengths())?Integer.toString(puertoSalidaNodoB.getMaxNumberOfWavelengths()):"Problema leyendo el numero de λ.");
        propiedadesDeEnlace.add(propWavelengths);
        
        tblPropiedadesDispositivo.cargarPropiedades(propiedadesDeEnlace);
    }

    @Override
    public void updatePropiedad(String id, String valor) {

        if(id.equalsIgnoreCase("linkSpeedAB")){
            puertoSalidaNodoA.setLinkSpeed(Double.valueOf(valor));
        }else if(id.equalsIgnoreCase("linkSpeedBA")){
            puertoSalidaNodoB.setLinkSpeed(Double.valueOf(valor));
        }else if(id.equalsIgnoreCase("switchingSpeed")){
            puertoSalidaNodoA.setSwitchingSpeed(Double.valueOf(valor));
            puertoSalidaNodoB.setSwitchingSpeed(Double.valueOf(valor));
        }else if(id.equalsIgnoreCase("defaultWavelengths")){
            puertoSalidaNodoA.setMaxNumberOfWavelengths(Integer.parseInt(valor));
            puertoSalidaNodoB.setMaxNumberOfWavelengths(Integer.parseInt(valor));
        }
    }
}
