package com.sbh.bpm.service;

import java.io.InputStream;

import javax.ws.rs.FormParam;

// import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class MultipartBody {
    public MultipartBody() {}

    @FormParam("file")
    // @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;

    @FormParam("fileName")
    // @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
}
