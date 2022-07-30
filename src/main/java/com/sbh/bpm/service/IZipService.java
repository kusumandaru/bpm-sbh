package com.sbh.bpm.service;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface IZipService {
  void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException;
}