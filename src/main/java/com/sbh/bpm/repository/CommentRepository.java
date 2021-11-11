package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.Comment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {
  List<Comment> findByCriteriaScoringID(Integer criteriaScoringId);
  List<Comment> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds);
}

