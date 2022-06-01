package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.sbh.bpm.service.IGoogleCloudStorage;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/gcs")
public class GoogleCloudStorageController {
  private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorageController.class);

  @Autowired
  private IGoogleCloudStorage cloudStorage;

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/read")
	public Response ReadGcsFile(@FormParam("file_name") String fileName) {    
    // Get it by blob name
    try {
      cloudStorage.InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = cloudStorage.GetBlobByName(fileName);

    cloudStorage.SetGcsSignUrl(blob);
    String publicUrl = cloudStorage.GetSignedUrl();
    // String json = new Gson().toJson(value);
    return Response.ok(publicUrl).build();
	}

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/write")
	public Response WriteGcsFile(@FormDataParam("file") InputStream file, @FormDataParam("file") FormDataContentDisposition fileFdcd) {    
    try {
      cloudStorage.InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    BlobId blobId = cloudStorage.SaveObject("general", fileFdcd.getFileName(), file);
    return Response.ok(blobId).build();
	}
}
