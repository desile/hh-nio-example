package ru.hh.school.http;

import org.apache.commons.io.FilenameUtils;
import ru.hh.school.utils.ConfigHandler;
import ru.hh.school.utils.FileHandler;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class HttpResponse {

    private static final String version = "HTTP/1.1";
    private static final String allowedMethods = "GET";
    private HttpResponseCode responseCode;
    private String contentType = "text/html";
    private MappedByteBuffer content;

    public HttpResponse(HttpRequest httpRequest){
        if(!allowedMethods.equals(httpRequest.getMethod())) {
            responseCode = HttpResponseCode.METHOD_NOT_ALLOWED;
            System.out.println(responseCode);
            return;
        }

        String requestedUri = httpRequest.getUri();
        if(requestedUri.contains("?")){
            responseCode = HttpResponseCode.BAD_REQUEST;
            System.out.println(responseCode);
            return;
        }

        responseCode = HttpResponseCode.OK;
        String resourcePath = ConfigHandler.getProperty(ConfigHandler.RESOURCE_PATH).orElse(".");
        String fileExtension = FilenameUtils.getExtension(requestedUri);
        String filePath = resourcePath + requestedUri;
        try {
            content = FileHandler.getFile(filePath);//FileUtils.readFileToByteArray(requestedFile);
            switch (fileExtension) {
                case "html":
                    contentType = "text/html";
                    break;
                case "js":
                    contentType = "application/javascript";
                    break;
                case "jpeg":
                case "jpg":
                    contentType = "image/jpeg";
                    break;
                default:
                    contentType = "application/octet-stream";
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseCode = HttpResponseCode.NOT_FOUND;
        } finally {
            System.out.println(responseCode);
        }
    }

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

}
