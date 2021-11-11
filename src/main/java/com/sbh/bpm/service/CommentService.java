package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Comment;
import com.sbh.bpm.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements ICommentService {
  @Autowired
  private CommentRepository repository;

  @Override
  public List<Comment> findAll() {
    return (List<Comment>) repository.findAll();
  }

  @Override
  public Comment findById(Integer commentId) {
    return repository.findById(commentId).get();
  }

  @Override
  public Comment save(Comment comment) {
    return repository.save(comment);
  }

  @Override
  public Iterable<Comment> saveAll(List<Comment> comments) {
    return repository.saveAll(comments);
  }

  @Override
  public List<Comment> findByCriteriaScoringID(Integer criteriaScoringId) {
    return repository.findByCriteriaScoringID(criteriaScoringId);
  }

  @Override
  public List<Comment> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds) {
    return (List<Comment>) repository.findByCriteriaScoringIDIn(criteriaScoringIds);
  }
}
