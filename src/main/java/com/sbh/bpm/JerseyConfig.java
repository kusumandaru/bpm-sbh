package com.sbh.bpm;

import javax.ws.rs.ApplicationPath;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/engine-rest")
public class JerseyConfig extends CamundaJerseyResourceConfig {
  @Override
  protected void registerAdditionalResources() {    
    register(CustomController.class);
    register(NewBuildingController.class);
    register(GoogleCloudStorageController.class);
    register(MultiPartFeature.class);
  }
}