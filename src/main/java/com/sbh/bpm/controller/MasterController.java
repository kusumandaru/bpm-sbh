package com.sbh.bpm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.google.cloud.storage.Blob;
import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.MasterAdmin;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMasterAdminService;
import com.sbh.bpm.service.IProvinceService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.CaseUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/master")
public class MasterController extends GcsUtil{
  private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IBuildingTypeService buildingTypeService;

  @Autowired
  private IMasterAdminService masterAdminService;

  @GET
  @Path(value = "/provinces")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allProvinces(@HeaderParam("Authorization") String authorization) {      
    List<Province> provinces = (List<Province>) provinceService.findAll();

    String json = new Gson().toJson(provinces);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/provinces/{province_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProvince(@HeaderParam("Authorization") String authorization, @PathParam("province_id") Integer provinceId) {      
    Province province = (Province) provinceService.findById(provinceId);

    String json = new Gson().toJson(province);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/provinces")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveProvinces(@HeaderParam("Authorization") String authorization,
                               Province province) {   
    province.setCreatedAt(new Date());
    province = provinceService.save(province);

    String json = new Gson().toJson(province);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/provinces/{province_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProvinces(@HeaderParam("Authorization") String authorization,
                               @PathParam("province_id") Integer provinceId, 
                               Province province) {   
    
    Province prov = (Province) provinceService.findById(provinceId);
    if (prov == null) {
      return Response.status(400, "province not found").build();
    }

    prov.setName(province.getName());
    prov = provinceService.save(prov);

    String json = new Gson().toJson(prov);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/cities")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCities(@HeaderParam("Authorization") String authorization) {      
    List<City> cities = (List<City>) cityService.findAll();

    String json = new Gson().toJson(cities);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/cities/{city_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCity(@HeaderParam("Authorization") String authorization, @PathParam("city_id") Integer cityId) {      
    City city = (City) cityService.findById(cityId);

    String json = new Gson().toJson(city);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/provinces/{province_id}/cities")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCitiesByProvince(@HeaderParam("Authorization") String authorization, @PathParam("province_id") Integer provinceId) { 
    List<City> cities = (List<City>) cityService.findByProvinceId(provinceId);

    String json = new Gson().toJson(cities);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/cities")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCities(@HeaderParam("Authorization") String authorization,
                            City city) {   
    city.setCreatedAt(new Date());
    city = cityService.save(city);

    String json = new Gson().toJson(city);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/cities/{city_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateCities(@HeaderParam("Authorization") String authorization,
                                  @PathParam("city_id") Integer cityId, 
                                  City city) {   

    City ct = (City) cityService.findById(cityId);
    if (ct == null) {
      return Response.status(400, "city not found").build();
    }
    
    ct.setName(city.getName());
    ct.setProvinceId(city.getProvinceId());
    ct = cityService.save(ct);

    String json = new Gson().toJson(ct);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/building_types")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allBuildingTypes(@HeaderParam("Authorization") String authorization) {      
    List<BuildingType> buildingTypes = (List<BuildingType>) buildingTypeService.findAll();

    String json = new Gson().toJson(buildingTypes);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/building_types/{building_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBuildingType(@HeaderParam("Authorization") String authorization, @PathParam("building_type_id") Integer buildingTypeId) {      
    BuildingType buildingType = (BuildingType) buildingTypeService.findById(buildingTypeId);

    String json = new Gson().toJson(buildingType);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/building_types")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveBuildingType(@HeaderParam("Authorization") String authorization,
                              BuildingType buildingType) {
    buildingType.setCreatedAt(new Date());
    buildingType = buildingTypeService.save(buildingType);

    String json = new Gson().toJson(buildingType);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/building_types/{building_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProvinces(@HeaderParam("Authorization") String authorization,
                               @PathParam("building_type_id") Integer buildingTypeId, 
                               BuildingType buildingType) {   
    BuildingType bt = (BuildingType) buildingTypeService.findById(buildingTypeId);
    if (buildingType == null) {
      return Response.status(400, "buildingType not found").build();
    }

    bt.setNameId(buildingType.getNameId());
    bt.setNameEn(buildingType.getNameEn());
    bt.setCode(buildingType.getCode());
    bt = buildingTypeService.save(bt);

    String json = new Gson().toJson(bt);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/master_admins")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMasterAdmin(@HeaderParam("Authorization") String authorization) {      
    MasterAdmin masterAdmin = masterAdminService.findLast();

    String json = new Gson().toJson(masterAdmin);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/master_admins")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response CreateMasterAdmin(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("manager_name") String managerName,
    @FormDataParam("fa_template_id") Integer faTemplateID,
    @FormDataParam("dr_template_id") Integer drTemplateID,
    @FormDataParam("default_dr_level") Integer defaultDRLevel,
    @FormDataParam("default_fa_level") Integer defaultFALevel,
    @FormDataParam("manager_signature") InputStream managerSignature, 
    @FormDataParam("manager_signature") FormDataContentDisposition managerSignatureFdcd,
    @FormDataParam("registration_letter") InputStream registrationLetter, 
    @FormDataParam("registration_letter") FormDataContentDisposition registrationLetterFdcd,
    @FormDataParam("first_attachment") InputStream firstAttachment, 
    @FormDataParam("first_attachment") FormDataContentDisposition firstAttachmentFdcd,
    @FormDataParam("second_attachment") InputStream secondAttachment, 
    @FormDataParam("second_attachment") FormDataContentDisposition secondAttachmentFdcd,
    @FormDataParam("third_attachment") InputStream thirdAttachment, 
    @FormDataParam("third_attachment") FormDataContentDisposition thirdAttachmentFdcd,
    @FormDataParam("scoring_form") InputStream scoringForm, 
    @FormDataParam("scoring_form") FormDataContentDisposition scoringFormFdcd
    ) {         
 
    MasterAdmin masterAdmin = masterAdminService.findLast();

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Pair<String, String>>> listOfCallable = Arrays.asList(
                () -> UploadToGcs("admin", managerSignature, managerSignatureFdcd, "manager_signature"),
                () -> UploadToGcs("admin", registrationLetter, registrationLetterFdcd, "registration_letter"),
                () -> UploadToGcs("admin", firstAttachment, firstAttachmentFdcd, "first_attachment"),
                () -> UploadToGcs("admin", secondAttachment, secondAttachmentFdcd, "second_attachment"),
                () -> UploadToGcs("admin", thirdAttachment, thirdAttachmentFdcd, "third_attachment"),
                () -> UploadToGcs("admin", scoringForm, scoringFormFdcd, "scoring_form")
              );
    
    Map<String, String> results = new HashMap<String, String>();
    try {
      List<Future<Pair<String, String>>> futures = executor.invokeAll(listOfCallable);
      futures.stream().forEach(f -> {
          try {
            Pair<String, String> res = f.get();
            if (res != null) {
              results.put(res.getKey(), res.getValue());
             
            }
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
      });
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();
    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    try {
      for (Map.Entry<String, String> entry : results.entrySet()) {
        try {
          String methodName = "set" + CaseUtils.toCamelCase(entry.getKey(), true, '_');
          Method method = masterAdmin.getClass().getMethod(methodName, String.class);
          method.invoke(masterAdmin, entry.getValue());
        } catch (Exception e){
          throw new IllegalStateException(e);
        }
      };
    } catch (Exception e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    if (managerName != null) {
      masterAdmin.setManagerName(managerName);
    }
    if (faTemplateID != null) {
      masterAdmin.setFaTemplateID(faTemplateID);
    }
    if (drTemplateID != null) {
      masterAdmin.setDrTemplateID(drTemplateID);
    }
    if (defaultDRLevel != null) {
      masterAdmin.setDefaultDRLevel(defaultDRLevel);
    }
    if (defaultFALevel != null) {
      masterAdmin.setDefaultFALevel(defaultFALevel);
    }
    masterAdmin = masterAdminService.save(masterAdmin);

    String json = new Gson().toJson(masterAdmin);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/url_file/{file_name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("file_name") String fileName
  ) {
    Map<String, Object> variableMap = masterAdminService.getVariableMap();
    
    Pair<String, String> result;
    try {
      result = GetUrlGcs(variableMap, "admin", fileName);
    } catch (IOException e) {
      result = null;
      return Response.status(404).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result.getValue());
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/registered_project_attachment")
  public Response GetRegisteredProjectAttachment(@HeaderParam("Authorization") String authorization) { 
    Map<String, Object> variableMap = masterAdminService.getVariableMap();

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Blob>> listOfCallable = Arrays.asList(
                () -> GetBlobDirect(variableMap, "admin", "first_attachment"),
                () -> GetBlobDirect(variableMap, "admin", "second_attachment"),
                () -> GetBlobDirect(variableMap, "admin", "third_attachment")
                );

    FileOutputStream fos;
    ZipOutputStream zipOut;
    String zipfilename = "registered_project_attachment" + ".zip";
    try {
      fos = new FileOutputStream(zipfilename);
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    try {
      List<Future<Blob>> futures = executor.invokeAll(listOfCallable);

      futures.stream().forEach(f -> {
          try {
            Blob blob = f.get();
            if (blob != null) {
              ZipEntry zipEntry = new ZipEntry(blob.getName());
              zipOut.putNextEntry(zipEntry);
              byte[] byteArray = blob.getContent();
              zipOut.write(byteArray);
            }
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
      });

    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    try {
      zipOut.close();
      fos.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
  

    File zipFile = new File(zipfilename);
    StreamingOutput stream = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException {
            try {
                output.write(IOUtils.toByteArray(new FileInputStream(zipFile)));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    };

    return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "inline; filename=\"" + zipFile.getName() + "\"") 
        .build();
  }
}
