package com.sbh.bpm.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IProvinceService;

import org.springframework.beans.factory.annotation.Autowired;


@Path(value = "/master")
public class MasterController {

  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

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
  public Response getProvince(@HeaderParam("Authorization") String authorization, @PathParam("province_id") String provinceId) {      
    Province province = (Province) provinceService.findyById(provinceId);

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
  public Response getCity(@HeaderParam("Authorization") String authorization, @PathParam("city_id") String cityId) {      
    City city = (City) cityService.findyById(cityId);

    String json = new Gson().toJson(city);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/provinces/{province_id}/cities")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCitiesByProvince(@HeaderParam("Authorization") String authorization, @PathParam("province_id") String provinceId) { 
    List<City> cities = (List<City>) cityService.findByProvinceId(provinceId);

    String json = new Gson().toJson(cities);
    return Response.ok(json).build();
  }
}
