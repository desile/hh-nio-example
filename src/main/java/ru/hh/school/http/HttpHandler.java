package ru.hh.school.http;


import org.apache.commons.io.FilenameUtils;
import ru.hh.school.utils.ConfigHandler;
import ru.hh.school.utils.FileHandler;

import java.io.IOException;

public class HttpHandler {

    private static final String allowedMethods = "GET";

    public HttpResponse serveRequest(HttpRequest httpRequest){
        HttpResponse httpResponse = new HttpResponse();

        if(!allowedMethods.equals(httpRequest.getMethod())) {
            httpResponse.setResponseCode(HttpResponseCode.METHOD_NOT_ALLOWED);
            return httpResponse;
        }

        String requestedUri = httpRequest.getUri();
        if(requestedUri.contains("?")){
            httpResponse.setResponseCode(HttpResponseCode.BAD_REQUEST);
            return httpResponse;
        }

        String resourcePath = ConfigHandler.getProperty(ConfigHandler.RESOURCE_PATH).orElse(".");
        String fileExtension = FilenameUtils.getExtension(requestedUri);
        String filePath = resourcePath + requestedUri;
        String contentType;
        try {
            httpResponse.setContent(FileHandler.getFile(filePath));//FileUtils.readFileToByteArray(requestedFile);
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
            httpResponse.setContentType(contentType);
            httpResponse.setResponseCode(HttpResponseCode.OK);
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.setResponseCode(HttpResponseCode.NOT_FOUND);
        }
        return httpResponse;
    }

}
