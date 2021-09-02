package com.sbh.bpm.service;

import java.io.IOException;
import java.util.Map;

import com.google.api.client.util.ArrayMap;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.template.Configuration;

@Service
public class MailerService implements IMailerService {
  private static final Logger logger = LogManager.getLogger(MailerService.class);

  @Autowired     
  Configuration fmConfiguration;

  @Value("${sendgrid.key}")
  String sendgridKey;

  public Response SendRejectionEmail(String rejectionNote) {
    Email from = new Email("mail@kusumanda.ru");
    String subject = "Submission Dokumen FA Greenship NB 1.2 Proyek Alfa Tower #Hasil Verifikasi";
    Email to = new Email("angga.kusumandaru@gmail.com");

    Map <String, Object> body = new ArrayMap <String, Object>();
    body.put("firstName", "Eva");
    body.put("lastName", "Cicilia");
    body.put("admin", "Rini");
    body.put("rejectionNote", rejectionNote);
    body.put("subject", subject);
    body.put("url", "http://sertifikasibangunanhijau.com/");
    
    Content content = new Content("text/html", geContentFromTemplate(body));
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(sendgridKey);
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      return response;
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public String geContentFromTemplate(Map <String, Object>model) { 
    StringBuffer content = new StringBuffer();

    try {
        content.append(FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email-template.flth"), model));
    } catch (Exception e) {
        logger.error(e.getMessage());
    }
    return content.toString();
  }

}
