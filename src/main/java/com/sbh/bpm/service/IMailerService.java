package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.PasswordToken;
import com.sbh.bpm.model.User;
import com.sendgrid.Response;

import org.camunda.bpm.engine.task.Task;

public interface IMailerService {
  List<Response> SendRejectionEmail(String rejectionNote, Task task);
  Response SendRegisterEmail(User user);
  Response SendUserCreationEmail(User user, String password);
  Response SendResetPasswordEmail(User user, PasswordToken passwordToken);
}
