package com.gramevapp.web.service;

import com.gramevapp.web.model.DiagramData;
import com.gramevapp.web.model.Run;
import com.gramevapp.web.repository.DiagramDataRepository;
import com.gramevapp.web.repository.RunRepository;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("diagramDataService")
@Transactional
@DynamicUpdate
public class DiagramDataService {

    @Autowired
    private DiagramDataRepository diagramRepository;

    @Autowired
    private RunRepository runRepository;


    public DiagramData findByRunId(Run runId) {
        return diagramRepository.findByRunId(runId);
    }

    public DiagramData saveDiagram(DiagramData diagramData) {
        return diagramRepository.save(diagramData);
    }

    public void saveRun(Run run) {
        runRepository.save(run);
    }

    public void deleteDiagram(DiagramData diagramDataId) {
        diagramRepository.delete(diagramDataId);
    }
}
