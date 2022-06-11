package com.appskimo.app.stream.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Slf4j
@RestController
public class FileController {

    private static final String ROOT = "/Users/dominic/temp/";

    @SneakyThrows
    @PostMapping("/upload")
    public void upload(final HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            log.debug("is not multipart request");
            return;
        }

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator itemIterator = upload.getItemIterator(request);

        while (itemIterator.hasNext()) {
            FileItemStream item = itemIterator.next();
            String name = item.getName();

            try (InputStream fileStream = item.openStream()) {
                if (!item.isFormField()) {
                    log.debug("File field " + name + " with file name " + item.getName() + " detected.");

                    File uploadedFile = new File(ROOT + name);
                    FileOutputStream fos = new FileOutputStream(uploadedFile);
                    Streams.copy(fileStream, fos, true);
                }
            }
        }
    }

}
