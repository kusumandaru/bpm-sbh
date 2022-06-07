package com.sbh.bpm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.client.util.ArrayMap;
import com.sbh.bpm.model.PasswordToken;
import com.sbh.bpm.model.SbhTask;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;

@Service
public class MailerService implements IMailerService {
  private static final Logger logger = LoggerFactory.getLogger(MailerService.class);

  @Autowired     
  Configuration fmConfiguration;

  @Autowired
  private IMasterAdminService masterAdminService;

  @Autowired
  private IUserService userService;

  @Autowired
  HistoryService historyService;

  @Value("${sendgrid.key}")
  String sendgridKey;

  @Value("${sendgrid.sender}")
  String sendgridSender;

  @Value("${frontend.url}")
  String baseUrl;

  @Value("${email.reset_password_path}")
  String resetPasswordPath;

  public List<Response> SendRejectionEmail(String rejectionNote, Task task) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    SbhTask sbhTask = SbhTask.CreateFromTask(task);
    Map<String, Object> variableMap = taskService.getVariables(task.getId());
    sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
    HistoricTaskInstance previousTask = findPreviousTask(task.getProcessInstanceId());
    
    List<User> users = userService.findByTenantId(task.getTenantId());
    Email from = new Email(sendgridSender);
    String subject = "Submission Dokumen "+ previousTask.getName() +" Greenship "+ sbhTask.getCertificationType() +" Proyek " +sbhTask.getBuildingName()+" #Hasil Verifikasi";

    List<Response> responses = new ArrayList<Response>();
    users.stream().forEach(user -> {
      Email to = new Email(user.getEmail());
      Map <String, Object> body = new ArrayMap <String, Object>();
      body.put("fullName", user.getFullName());
      body.put("admin", masterAdminService.findLast().getManagerName());
      body.put("rejectionNote", rejectionNote);
      body.put("subject", subject);
      body.put("url", baseUrl);
      body.put("taskName", previousTask.getName());

      Content content = new Content("text/html", geContentFromTemplate(body, "email-verification-submission"));
      Mail mail = new Mail(from, subject, to, content);

      SendGrid sg = new SendGrid(sendgridKey);
      Request request = new Request();
      try {
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        responses.add(response);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    return responses;
  }

  public Response SendRegisterEmail(User u) {
    UserDetail user = userService.GetUserDetailFromId(u.getId());

    Email from = new Email(sendgridSender);
    String subject = "[Register] Selamat datang di Greenship Sertifikasi Bangunan Hijau";

    Email to = new Email(user.getEmail());
    Map <String, Object> body = new ArrayMap <String, Object>();
    body.put("fullName", user.getFullName());
    body.put("subject", subject);
    body.put("groupName", user.getGroup().getName());

    if (user.getTenant() != null) {
      body.put("tenantName", user.getTenant().getName());
    } else {
      body.put("tenantName", '-');
    }

    body.put("url", baseUrl);

    Content content = new Content("text/html", geContentFromTemplate(body, "email-register"));
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(sendgridKey);
    Request request = new Request();
    Response response = new Response();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      response = sg.api(request);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return response;
  }

  public Response SendUserCreationEmail(User u, String password) {
    UserDetail user = userService.GetUserDetailFromId(u.getId());

    Email from = new Email(sendgridSender);
    String subject = "[Register] Selamat datang di Greenship Sertifikasi Bangunan Hijau";

    Email to = new Email(user.getEmail());
    Map <String, Object> body = new ArrayMap <String, Object>();
    body.put("fullName", user.getFullName());
    body.put("subject", subject);
    body.put("groupName", user.getGroup().getName());
    body.put("password", password);
    if (user.getTenant() != null) {
      body.put("tenantName", user.getTenant().getName());
    } else {
      body.put("tenantName", '-');

    }

    body.put("url", baseUrl);

    Content content = new Content("text/html", geContentFromTemplate(body, "email-user-creation"));
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(sendgridKey);
    Request request = new Request();
    Response response = new Response();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      response = sg.api(request);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return response;
  }

  public Response SendResetPasswordEmail(User user, PasswordToken passwordToken) {
    Email from = new Email(sendgridSender);
    String subject = "[Reset Password] Akun Greenship Sertifikasi Bangunan Hijau";
    
    Email to = new Email(user.getEmail());
    Map <String, Object> body = new ArrayMap <String, Object>();
    body.put("subject", subject);
    body.put("reset_url", baseUrl+resetPasswordPath+passwordToken.getToken());

    Content content = new Content("text/html", geContentFromTemplate(body, "email-reset-password"));
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(sendgridKey);
    Request request = new Request();
    Response response = new Response();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      response = sg.api(request);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return response;
  }

  public String geContentFromTemplate(Map <String, Object>model, String emailTemplate) { 
    StringBuffer content = new StringBuffer();

    try {
        content.append(FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate(emailTemplate + ".flth"), model));
    } catch (Exception e) {
        logger.error(e.getMessage());
    }
    return content.toString();
  }

  public HistoricTaskInstance findPreviousTask(String processInstanceId) {
    return historyService.createHistoricTaskInstanceQuery().
            processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().list().get(0);
  }
}
