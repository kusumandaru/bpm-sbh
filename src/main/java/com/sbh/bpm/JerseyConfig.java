package com.sbh.bpm;

import javax.ws.rs.ApplicationPath;

import com.sbh.bpm.controller.AssessmentController;
import com.sbh.bpm.controller.DocumentBuildingController;
import com.sbh.bpm.controller.FileController;
import com.sbh.bpm.controller.GoogleCloudStorageController;
import com.sbh.bpm.controller.MasterController;
import com.sbh.bpm.controller.MasterProjectController;
import com.sbh.bpm.controller.NewBuildingController;
import com.sbh.bpm.controller.ProjectController;
import com.sbh.bpm.controller.TaskController;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/engine-rest")
public class JerseyConfig extends CamundaJerseyResourceConfig {
  @Override
  protected void registerAdditionalResources() {   
    register(MultiPartFeature.class);
    register(MasterController.class);
    register(MasterProjectController.class);
    register(GoogleCloudStorageController.class);
    register(FileController.class);
    register(NewBuildingController.class);
    register(AssessmentController.class);
    register(ProjectController.class);
    register(TaskController.class);
    register(DocumentBuildingController.class);
  }
}