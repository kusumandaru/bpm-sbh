package com.sbh.bpm.service;

import java.util.Map;

public interface IPdfGeneratorUtil {
  byte[] CreatePdf(String templateName, Map map);
}
