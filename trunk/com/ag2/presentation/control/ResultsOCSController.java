/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ag2.presentation.control;

import Grid.Entity;
import Grid.OCS.stats.ManagerOCS;
import Grid.OCS.stats.ManagerOCS.SumaryOCS;
import com.ag2.controller.MatchCoupleObjectContainer;
import com.ag2.controller.ResultsAbstractOCSController;
import com.ag2.presentation.design.GraphNode;
import com.ag2.util.Utils;
import java.util.ArrayList;

/**
 *
 * @author Frank
 */
public class ResultsOCSController extends ResultsAbstractOCSController {

    ArrayList<ManagerOCS.SumaryOCS> sumaryOCSs;
    ArrayList<ManagerOCS.InstanceOCS> instanceOCSs;

    @Override
    public int sizeSummaryOCS() {

        if (sumaryOCSs == null) {
            sumaryOCSs = ManagerOCS.getInstance().getListSummaryOCS();
        }
        return sumaryOCSs.size();
    }

    @Override
    public void loadOCS_SummaryByIndex(int index) {
        if (sumaryOCSs == null) {
            sumaryOCSs = ManagerOCS.getInstance().getListSummaryOCS();
        }
        SumaryOCS sumaryOCS = sumaryOCSs.get(index);

        gnSummarySource = Utils.findNodeGraphByOriginalName(sumaryOCS.getSourceDestination().getEntitySource().getId());
        gnSummaryDestination = Utils.findNodeGraphByOriginalName(sumaryOCS.getSourceDestination().getEntityDestination().getId());

        requestedSummaryOCS = sumaryOCS.getCountRequestOCS();
        createdSummaryOCS = sumaryOCS.getCountCreateOCS();
        faultSummaryOCS = sumaryOCS.getCountFaultOCS();
        timeDuracionSummaryOCS = sumaryOCS.getCountAverageDurationTimeOCS();

    }

    @Override
    public int sizeInstanceOCS(GraphNode graphNodeSource, GraphNode graphNodeDestination) {
        Entity entitySource = MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(graphNodeSource);
        Entity entityDestination = MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(graphNodeDestination);
        ManagerOCS.SourceDestination sourceDestination = new ManagerOCS.SourceDestination(entitySource, entityDestination);

        return ManagerOCS.getInstance().getMapSumaryOCS().get(sourceDestination).getInstanceOCSs().size();
    }

    @Override
    public void loadOCS_InstanceByIndex(GraphNode graphNodeSource, GraphNode graphNodeDestination, int index) {

        Entity entitySource = MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(graphNodeSource);
        Entity entityDestination = MatchCoupleObjectContainer.getInstanceNodeMatchCoupleObjectContainer().get(graphNodeDestination);
        ManagerOCS.SourceDestination sourceDestination = new ManagerOCS.SourceDestination(entitySource, entityDestination);

        instanceOCSs = ManagerOCS.getInstance().getMapSumaryOCS().get(sourceDestination).getInstanceOCSs();


        ManagerOCS.InstanceOCS instanceOCS = instanceOCSs.get(index);

        pathInstaceOCS = new ArrayList<GraphNode>();
        
        for(Entity entity : instanceOCS.getRoute()  )
        {            
            pathInstaceOCS.add( Utils.findNodeGraphByOriginalName(entity.getId()));            
        }
              
        requestTimeInstanceOCS = instanceOCS.getRequestTimeInstanceOCS();
        setupTimeInstanceOCS  = instanceOCS.getSetupTimeInstanceOCS();
        durationTimeInstanceOCS = instanceOCS.getDurationTimeInstanceOCS();
        tearDownTimeInstanceOCS = instanceOCS.getTearDownTimeInstanceOCS();
        trafficInstanceOCS = instanceOCS.getTrafficInstanceOCS();
        problemInstanceOCS = instanceOCS.getProblemInstanceOCS() ;
        if( instanceOCS.getNodeErrorInstanceOCS()!=null)
        {
            nodeErrorInstanceOCS =  Utils.findNodeGraphByOriginalName( instanceOCS.getNodeErrorInstanceOCS().getId());
        }
        

    }
    @Override
    public void clean()
    {
        sumaryOCSs=null;
        instanceOCSs=null;
        ManagerOCS.clean();
    }
}
