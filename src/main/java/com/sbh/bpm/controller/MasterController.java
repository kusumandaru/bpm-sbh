package com.sbh.bpm.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IProvinceService;

import org.springframework.beans.factory.annotation.Autowired;


@Path(value = "/master")
public class MasterController {

  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IBuildingTypeService buildingTypeService;

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
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response saveProvinces(@HeaderParam("Authorization") String authorization,
                               @FormParam("name") String name) {   
    Province province = new Province();
    province.setName(name);
    province = provinceService.save(province);

    String json = new Gson().toJson(province);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/provinces/{province_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updateProvinces(@HeaderParam("Authorization") String authorization,
                               @PathParam("province_id") Integer provinceId, 
                               @FormParam("name") String name) {   
    
    Province province = (Province) provinceService.findById(provinceId);
    if (province == null) {
      return Response.status(400, "province not found").build();
    }

    province.setName(name);
    province = provinceService.save(province);

    String json = new Gson().toJson(province);
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
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response saveCities(@HeaderParam("Authorization") String authorization,
                               @FormParam("name") String name,
                               @FormParam("province_id") Integer provinceId) {   
    City city = new City();
    city.setName(name);
    city.setProvinceId(provinceId);

    city = cityService.save(city);

    String json = new Gson().toJson(city);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/cities/{city_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updateCities(@HeaderParam("Authorization") String authorization,
                                  @FormParam("name") String name,
                                  @FormParam("province_id") Integer provinceId,
                                  @PathParam("city_id") Integer cityId) {   

    City city = (City) cityService.findById(cityId);
    if (city == null) {
      return Response.status(400, "city not found").build();
    }
    
    city.setName(name);
    city.setProvinceId(provinceId);
    city = cityService.save(city);

    String json = new Gson().toJson(city);
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
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response saveBuildingType(@HeaderParam("Authorization") String authorization,
                               @FormParam("code") String code,
                               @FormParam("name_id") String nameId,
                               @FormParam("name_en") String nameEn) {   
    BuildingType buildingType = new BuildingType();
    buildingType.setNameId(nameId);
    buildingType.setNameEn(nameEn);
    buildingType.setCode(code);
    buildingType = buildingTypeService.save(buildingType);

    String json = new Gson().toJson(buildingType);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/building_types/{building_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updateProvinces(@HeaderParam("Authorization") String authorization,
                               @PathParam("building_type_id") Integer buildingTypeId, 
                               @FormParam("code") String code,
                               @FormParam("name_id") String nameId,
                               @FormParam("name_en") String nameEn) {   
    BuildingType buildingType = (BuildingType) buildingTypeService.findById(buildingTypeId);
    if (buildingType == null) {
      return Response.status(400, "buildingType not found").build();
    }

    buildingType.setNameId(nameId);
    buildingType.setNameEn(nameEn);
    buildingType.setCode(code);
    buildingType = buildingTypeService.save(buildingType);

    String json = new Gson().toJson(buildingType);
    return Response.ok(json).build();
  }
}
