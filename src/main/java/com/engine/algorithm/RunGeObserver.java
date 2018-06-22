package com.engine.algorithm;

import com.gramevapp.web.model.DiagramData;
import com.gramevapp.web.model.DiagramPair;
import com.gramevapp.web.model.Run;
import com.gramevapp.web.other.BeanUtil;
import com.gramevapp.web.service.DiagramDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.*;

/**
 * Stores into the database the values generated by RunGE execution.
 *
 */

@Service("RunGe")
public class RunGeObserver implements Observer {

    private DiagramData diagramData;

    // DATE TIMESTAMP
    Calendar calendar = Calendar.getInstance();
    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
    // END - DATE TIMESTAMP

    @Override
    public void update(Observable o, Object arg) {
        HashMap<String, Object> dataMap = (HashMap<String, Object>) arg;

        // Taking data from dictionary
        // Actual generation
        int currGen = Integer.valueOf(dataMap.get("CurrentGeneration").toString());
        // Porcentaje de ejecución Execution percentage
        int currPercent = Math.round((currGen * 100) / Integer.valueOf(dataMap.get("MaxGenerations").toString()));

        // One of these two are null

        // Current value of best individual
        double currBest = Double.valueOf(dataMap.get("BestObjective").toString());

        // Multi-objetive shows all objectives (Not necessary but do not erase)
        double objs[][] = (double [][]) dataMap.get("Objectives");

        // http://codippa.com/how-to-autowire-objects-in-non-spring-classes/
        //get application context
        ApplicationContext context = BeanUtil.getAppContext();
        DiagramDataService dataDataService = (DiagramDataService) context.getBean("diagramDataService");

        DiagramPair diagramPair = new DiagramPair(currBest, currGen);

        this.diagramData.addListPair(diagramPair);

        if(this.diagramData.getRunId().getStatus().equals(Run.Status.INITIALIZING)){
            dataDataService.deleteAllByDiagramId(this.diagramData);
            this.diagramData.setListPair(new ArrayList<>());    // All the points were being added into the arrayList. But it needs to starts again.

            this.diagramData.getRunId().setStatus(Run.Status.RUNNING);
        }

        if(this.diagramData.getRunId().getStatus().equals(Run.Status.STOPPED))
            this.diagramData.setStopped(true);

        if(this.diagramData.getRunId().getStatus().equals(Run.Status.FAILED))
            this.diagramData.setFailed(true);

        if(currPercent == 100 || currBest <= 0.0) {

            this.diagramData.setFinished(true);
        }

        if(currBest > 0.0)
            this.diagramData.setBestIndividual(currBest);
        else
            this.diagramData.setBestIndividual(0.0);
        this.diagramData.setCurrentGeneration(currGen);
        this.diagramData.setTime(currentTimestamp);

        dataDataService.saveDiagramPair(diagramPair);

        dataDataService.saveDiagram(this.diagramData);

        /*
        if (dataMap.get("BestObjective") != null) {
            currBest = Double.valueOf(dataMap.get("BestObjective").toString());

        } else if (dataMap.get("Objectives") != null) {
            double objs[][] = (double [][]) dataMap.get("Objectives");
        }
         */
    }

    public DiagramData getDiagramData() {
        return diagramData;
    }

    public void setDiagramData(DiagramData diagramData) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.diagramData = diagramData;
    }
}
