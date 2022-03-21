package com.sbh.bpm.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class PaginationRequest {
  @Getter @Setter
  @SerializedName("page")
  private Integer page;

  @Getter @Setter
  @SerializedName("size")
  private Integer size;

  @Getter @Setter
  @SerializedName("sort")
  private SortType[] sort;

  @Getter @Setter
  @SerializedName("filter")
  private Filter filter;

  public static class SortType {
    @Getter @Setter
    @SerializedName("field")
    private String field;
  
    @Getter @Setter
    @SerializedName("type")
    private String type;
  }

  public static class Filter {
    @Getter @Setter
    @SerializedName("role")
    private String role;

    @Getter @Setter
    @SerializedName("name")
    private String name;

    @Getter @Setter
    @SerializedName("certification_type")
    private String certificationType;

    @Getter @Setter
    @SerializedName("building_type_name")
    private String buildingTypeName;

    @Getter @Setter
    @SerializedName("building_name")
    private String buildingName;

    @Getter @Setter
    @SerializedName("assignee")
    private String assignee;
  }
}
