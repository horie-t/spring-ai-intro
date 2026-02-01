package com.example.spring_ai_demo.adapter.in.web.dto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import java.util.Base64;

public record DataUri(MimeType mimeType, byte[] data) {

    public static DataUri parse(String dataUriString) {
        if (!StringUtils.hasText(dataUriString) || !dataUriString.startsWith("data:")) {
            throw new IllegalArgumentException("Invalid Data URI");
        }

        int commaIndex = dataUriString.indexOf(',');
        if (commaIndex == -1) {
            throw new IllegalArgumentException("Invalid Data URI: missing comma");
        }

        String header = dataUriString.substring(0, commaIndex);
        String dataPart = dataUriString.substring(commaIndex + 1);

        int semiColonIndex = header.indexOf(';');
        String mimeType = (semiColonIndex != -1)
                ? header.substring(5, semiColonIndex)
                : "text/plain";

        byte[] decodedData;
        if (header.contains(";base64")) {
            decodedData = Base64.getDecoder().decode(dataPart);
        } else {
            throw new UnsupportedOperationException("Only base64 supported");
        }

        return new DataUri(MimeTypeUtils.parseMimeType(mimeType), decodedData);
    }

    public Resource toResource() {
        return new ByteArrayResource(this.data) {
            @Override
            public String getFilename() {
                String ext = mimeType.toString().replace("image/", "");
                return "upload." + ext;
            }
        };
    }
}