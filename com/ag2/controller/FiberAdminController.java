package com.ag2.controller;

import Grid.Entity;
import Grid.Port.GridOutPort;
import com.ag2.model.FiberCreationModel;
import com.ag2.model.LinkCreationAbstractModel;
import com.ag2.model.PhosphorusLinkModel;
import com.ag2.model.SimulationBase;
import com.ag2.presentation.design.ClientGraphNode;
import com.ag2.presentation.design.GraphLink;
import com.ag2.presentation.design.GraphNode;
import com.ag2.presentation.design.property.EntityProperty;
import java.util.ArrayList;
import java.util.HashMap;
import simbase.Port.SimBaseInPort;
import simbase.SimBaseEntity;

public class FiberAdminController extends LinkAdminAbstractController {

    private PhosphorusLinkModel lastPhosphorusLinkModelCreated = null;

    @Override
    public boolean createLink(GraphNode sourceGraphNode, GraphNode destinationGraphNode) {

        Entity phosphorusNodeA;
        Entity phosphorusNodeB;

        for (LinkCreationAbstractModel linkCreationModel : linkCreationAbstractModels) {

            if (linkCreationModel instanceof FiberCreationModel) {

                lastPhosphorusLinkModelCreated = null;
                
                phosphorusNodeA = (Entity) MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(sourceGraphNode);
                phosphorusNodeB = (Entity) MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(destinationGraphNode);

                if (phosphorusNodeA != null && phosphorusNodeB != null) {
                    lastPhosphorusLinkModelCreated = (PhosphorusLinkModel) linkCreationModel.createLink(phosphorusNodeA, phosphorusNodeB);
//                    MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer().put(graphLink, phosphorusLinkModel);
                } else {
                    System.out.println("Algun nodo PHOSPHOROUS esta NULL, NO se creo el ENLACE en PHOPHOROUS.");
                }

                return (lastPhosphorusLinkModelCreated == null) ? false : true;
            }
        }


        return false;
    }

    @Override
    public void queryProperty(GraphLink graphLink) {

        ArrayList<EntityProperty> entityPropertys = new ArrayList<EntityProperty>();
        PhosphorusLinkModel phosphorusSelectedLinkModel = (PhosphorusLinkModel) MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer().get(graphLink);
        GridOutPort gridOutPortA = phosphorusSelectedLinkModel.getGridOutPortA();
        GridOutPort gridOutPortB = phosphorusSelectedLinkModel.getGridOutPortB();
        /*
         **Enlace de nodo Phosphorous de A hacia B (A->B)
         */
        //===========================================================================================================
        EntityProperty linkAB_NameProperty = new EntityProperty("channelDirectionAB", "Dirección del Canal:", EntityProperty.PropertyType.LABEL, false);
        linkAB_NameProperty.setFirstValue(graphLink.getGraphNodeA().getName() + "-->" + graphLink.getGraphNodeB().getName());
        entityPropertys.add(linkAB_NameProperty);

        EntityProperty linkABSpeedProperty = new EntityProperty("linkSpeedAB", "Vel. del Enlace:", EntityProperty.PropertyType.NUMBER, false);
        linkABSpeedProperty.setFirstValue(Double.toString(gridOutPortA.getLinkSpeed()));
        entityPropertys.add(linkABSpeedProperty);
        //===========================================================================================================

        /*
         **Enlace de nodo Phosphorous de B hacia A (B->A)
         */
        //===========================================================================================================
        EntityProperty linkBA_NameProperty = new EntityProperty("channelDirectionBA", "Dirección del Canal:", EntityProperty.PropertyType.LABEL, false);
        linkBA_NameProperty.setFirstValue(graphLink.getGraphNodeB().getName() + "-->" + graphLink.getGraphNodeA().getName());
        entityPropertys.add(linkBA_NameProperty);

        EntityProperty linkBASpeedProperty = new EntityProperty("linkSpeedBA", "Vel. del Enlace:", EntityProperty.PropertyType.NUMBER, false);
        linkBASpeedProperty.setFirstValue(Double.toString(gridOutPortB.getLinkSpeed()));
        entityPropertys.add(linkBASpeedProperty);
        //===========================================================================================================

        /*
         **Propiedades comunes en ambas direcciones del canal.
         */
        EntityProperty switchingSpeedProperty = new EntityProperty("switchingSpeed", "Vel. de Conmutación:", EntityProperty.PropertyType.NUMBER, false);
        switchingSpeedProperty.setFirstValue((gridOutPortA.getSwitchingSpeed() == gridOutPortB.getSwitchingSpeed()) ? Double.toString(gridOutPortB.getSwitchingSpeed()) : "Problema leyendo Vel. de conmutación.");
        entityPropertys.add(switchingSpeedProperty);

        EntityProperty wavelengthsProperty = new EntityProperty("defaultWavelengths", "Cantidad de λs:", EntityProperty.PropertyType.NUMBER, false);
        wavelengthsProperty.setFirstValue((gridOutPortA.getMaxNumberOfWavelengths() == gridOutPortB.getMaxNumberOfWavelengths()) ? Integer.toString(gridOutPortB.getMaxNumberOfWavelengths()) : "Problema leyendo el numero de λ.");
        entityPropertys.add(wavelengthsProperty);

        entityPropertyTable.loadProperties(entityPropertys);
    }

    @Override
    public void updatePropiedad(GraphLink graphLink, String id, String valor) {

        graphLink.getProperties().put(id, valor);

        PhosphorusLinkModel phosphorusLinkModelSelected = (PhosphorusLinkModel) MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer().get(graphLink);

        if (id.equalsIgnoreCase("linkSpeedAB")) {
            phosphorusLinkModelSelected.getGridOutPortA().setLinkSpeed(Double.valueOf(valor));
        } else if (id.equalsIgnoreCase("linkSpeedBA")) {
            phosphorusLinkModelSelected.getGridOutPortB().setLinkSpeed(Double.valueOf(valor));
        } else if (id.equalsIgnoreCase("switchingSpeed")) {
            phosphorusLinkModelSelected.getGridOutPortA().setSwitchingSpeed(Double.valueOf(valor));
            phosphorusLinkModelSelected.getGridOutPortB().setSwitchingSpeed(Double.valueOf(valor));
        } else if (id.equalsIgnoreCase("defaultWavelengths")) {
            phosphorusLinkModelSelected.getGridOutPortA().setMaxNumberOfWavelengths(Integer.parseInt(valor));
            phosphorusLinkModelSelected.getGridOutPortB().setMaxNumberOfWavelengths(Integer.parseInt(valor));
        }
    }

    @Override
    public void reCreatePhosphorousLinks() {


        HashMap<GraphLink, PhosphorusLinkModel> linkMatchCoupleObjectContainer = MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer();
        for (GraphLink graphLink : linkMatchCoupleObjectContainer.keySet()) {
            //createLink(graphLink); ojo
            
            createLink(graphLink.getGraphNodeA(),graphLink.getGraphNodeB());
        }

        for (GraphLink graphLink : linkMatchCoupleObjectContainer.keySet()) {

            for (String id : graphLink.getProperties().keySet()) {
                updatePropiedad(graphLink, id, graphLink.getProperties().get(id));
            }
        }
    }

    @Override
    public boolean removeLink(GraphLink graphLink) {

        HashMap<GraphLink, PhosphorusLinkModel> linkMatchCoupleObjectContainer = MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer();
        PhosphorusLinkModel phosLink = linkMatchCoupleObjectContainer.get(graphLink);

        boolean canRemovePortsInNodeA = removeInAndOutPort(phosLink.getGridOutPortA());
        boolean canRemovePortsInNodeB = removeInAndOutPort(phosLink.getGridOutPortB());

        linkMatchCoupleObjectContainer.remove(graphLink);

        return (canRemovePortsInNodeA && canRemovePortsInNodeB);
    }

    private boolean removeInAndOutPort(GridOutPort outPort) {
        //Remuevo el puerto de salida y de entrada
        SimBaseInPort inPort = outPort.getTarget();

        SimBaseEntity source = outPort.getOwner();
        SimBaseEntity target = inPort.getOwner();

        boolean canRemoveInPort = target.getInPorts().remove(inPort);
        boolean canRemoveOutPort = source.getOutPorts().remove(outPort);

        return canRemoveInPort && canRemoveOutPort;
    }

    @Override
    public boolean canCreateLink(GraphNode sourceGraphNode, GraphNode targetGraphNode) {

        HashMap<GraphLink, PhosphorusLinkModel> linkMatchCoupleObjectContainer = MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer();

        for (GraphLink graphLink : linkMatchCoupleObjectContainer.keySet()) {

            GraphNode nodeInLinkA = graphLink.getGraphNodeA();
            GraphNode nodeInLinkB = graphLink.getGraphNodeB();

            //Verifico q no haya un enlace entre este par de nodos
            if ((sourceGraphNode.equals(nodeInLinkA) || sourceGraphNode.equals(nodeInLinkB))
                    && (targetGraphNode.equals(nodeInLinkA) || targetGraphNode.equals(nodeInLinkB))) {
                return false;
            }

            //Verifico q los nodos clientes no puedan tener mas de un enlace
            boolean isInPreviousLinkSourceNode = clientNodeExistInPreviousLink(sourceGraphNode, nodeInLinkA, nodeInLinkB);
            boolean isInPreviousLinkTargetNode = clientNodeExistInPreviousLink(targetGraphNode, nodeInLinkA, nodeInLinkB);

            if (isInPreviousLinkSourceNode || isInPreviousLinkTargetNode) {
                return false;
            }
        }

        return true;
    }

    private boolean clientNodeExistInPreviousLink(GraphNode graphNodeToCheck, GraphNode nodeInLinkA, GraphNode nodeInLinkB) {

        if (graphNodeToCheck instanceof ClientGraphNode) {

            if (graphNodeToCheck.equals(nodeInLinkA) || graphNodeToCheck.equals(nodeInLinkB)) {
                return true;
            }
        }

        return false;
    }

    public PhosphorusLinkModel getLastPhosphorusLinkModelCreated() {
        return lastPhosphorusLinkModelCreated;
    }

    public void setLastPhosphorusLinkModelCreated(PhosphorusLinkModel lastPhosphorusLinkModelCreated) {
        this.lastPhosphorusLinkModelCreated = lastPhosphorusLinkModelCreated;
    }

    public void insertCoupleLinksOnMatchContainer(GraphLink graphLink) {
        MatchCoupleObjectContainer.getInstanceLinkMatchCoupleObjectContainer().put(graphLink, lastPhosphorusLinkModelCreated);
        lastPhosphorusLinkModelCreated = null;
    }
}
