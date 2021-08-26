package com.sbh.bpm.service;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Map;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class PdfGeneratorUtil implements IPdfGeneratorUtil {

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${base.url}")
  String baseUrl;

	public byte[] CreatePdf(String templateName, Map data) {
		Assert.notNull(templateName, "The templateName can not be null");
		Context ctx = new Context();
		if (data != null) {
      Iterator itMap = data.entrySet().iterator();
      while (itMap.hasNext()) {
        Map.Entry pair = (Map.Entry) itMap.next();
        ctx.setVariable(pair.getKey().toString(), pair.getValue());
			}
		}

    String eligibleHtml = templateEngine.process(templateName, ctx);

    /* Setup Source and target I/O streams */

    ByteArrayOutputStream target = new ByteArrayOutputStream();
    ConverterProperties converterProperties = new ConverterProperties();
    converterProperties.setBaseUri(baseUrl);
    /* Call convert method */
    HtmlConverter.convertToPdf(eligibleHtml, target, converterProperties);

    /* extract output as bytes */
    byte[] bytes = target.toByteArray();
		return bytes;
	}
}