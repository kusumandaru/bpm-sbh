package com.sbh.bpm.service;

import com.sendgrid.Response;

public interface IMailerService {
  Response SendRejectionEmail(String rejectionNote);
}
