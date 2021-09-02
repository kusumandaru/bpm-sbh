package com.sbh.bpm;

import javax.ws.rs.ApplicationPath;

import com.sbh.bpm.controller.GoogleCloudStorageController;
import com.sbh.bpm.controller.MasterController;
import com.sbh.bpm.controller.NewBuildingController;
import com.sbh.bpm.controller.TaskController;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/engine-rest")
public class JerseyConfig extends CamundaJerseyResourceConfig {
  @Override
  protected void registerAdditionalResources() {    
    register(NewBuildingController.class);
    register(TaskController.class);
    register(GoogleCloudStorageController.class);
    register(MasterController.class);
    register(MultiPartFeature.class);
  }
}