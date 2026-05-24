package org.gca.schoolms.certificates;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class CertificatePdfService {

    private final TemplateEngine templateEngine;
    private final ResourceLoader resourceLoader;

    public CertificatePdfService(TemplateEngine templateEngine, ResourceLoader resourceLoader) {
        this.templateEngine = templateEngine;
        this.resourceLoader = resourceLoader;
    }

    public byte[] generate(String templateName, Context context) {
        try {
            String html = templateEngine.process(templateName, context);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            Resource baseResource = resourceLoader.getResource("classpath:/static/");
            builder.withHtmlContent(html, baseResource.getURL().toString());
            builder.toStream(outputStream);
            builder.useFastMode();
            builder.run();
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to generate certificate PDF", exception);
        }
    }
}
