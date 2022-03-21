package com.sbh.bpm.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class PaginationResult {
  @Getter @Setter
  @SerializedName("sbh_tasks")
  private List<SbhTask> sbhTasks;

  @Getter @Setter
  private Long count;

  public PaginationResult(List<SbhTask> sbhTasks, Long count) {
    this.sbhTasks = sbhTasks;
    this.count = count;
  }
}
