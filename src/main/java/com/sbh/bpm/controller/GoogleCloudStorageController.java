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
import com.sbh.bpm.service.GoogleCloudStorage;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value = "/gcs")
public class GoogleCloudStorageController {
  private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorageController.class);

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/read")
	public Response ReadGcsFile(@FormParam("file_name") String fileName) {    
    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    // Get it by blob name
    Blob blob = googleCloudStorage.GetBlobByName(fileName);

    googleCloudStorage.SetGcsSignUrl(blob);
    String publicUrl = googleCloudStorage.GetSignedUrl();
    // String json = new Gson().toJson(value);
    return Response.ok(publicUrl).build();
	}

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Path(value = "/write")
	public Response WriteGcsFile(@FormDataParam("file") InputStream file, @FormDataParam("file") FormDataContentDisposition fileFdcd) {    
    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    BlobId blobId = googleCloudStorage.SaveObject("general", fileFdcd.getFileName(), file);
    return Response.ok(blobId).build();
	}
}
