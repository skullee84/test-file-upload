package com.appskimo.app.stream.controller;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
@RestController
public class FileController {

    private static final String ROOT = "/Users/dominic/temp/";
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    @PostMapping(value = "/upload2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> up(final HttpServletRequest request) throws IOException, ServletException {
        Iterator<String> iterator = request.getHeaderNames().asIterator();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            log.debug("## {} {}", headerName, request.getHeader(headerName));
        }

        @Cleanup ServletInputStream inputStream = request.getInputStream();
        try (FileOutputStream outputStream = new FileOutputStream(ROOT + UUID.randomUUID(), false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

        return ResponseEntity.ok().build();
    }

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
