package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Comment;

public interface ICommentService {

  List<Comment> findAll();
  Comment findById(Integer commentId);
  Comment save(Comment comment);
  Iterable<Comment> saveAll(List<Comment> comments);
  List<Comment> findByCriteriaScoringID(Integer criteriaScoringId);
  List<Comment> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds);
}
