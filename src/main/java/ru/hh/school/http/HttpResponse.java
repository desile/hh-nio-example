package ru.hh.school.http;

import org.apache.commons.io.FilenameUtils;
import ru.hh.school.utils.ConfigHandler;
import ru.hh.school.utils.FileHandler;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class HttpResponse {

    private static final String version = "HTTP/1.1";
    private HttpResponseCode responseCode;
    private String contentType = "text/html";
    private MappedByteBuffer content;

    public String getVersion() {
        return version;
    }

    public HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public String getContentType() {
        return contentType;
    }

    public MappedByteBuffer getContent() {
        return content;
    }

    public void setResponseCode(HttpResponseCode responseCode) {
        System.out.println(responseCode);
        this.responseCode = responseCode;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContent(MappedByteBuffer content) {
        this.content = content;
    }
}
