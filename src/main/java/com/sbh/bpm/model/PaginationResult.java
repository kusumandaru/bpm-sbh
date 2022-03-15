package com.sbh.bpm.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class PaginationResult {
  @Getter @Setter
  private List<SbhTask> sbhTasks;

  @Getter @Setter
  private Long count;

  public PaginationResult(List<SbhTask> sbhTasks, Long count) {
    this.sbhTasks = sbhTasks;
    this.count = count;
  }
}
