package ru.hh.school.http;

public class HttpRequest {

    private String method;
    private String uri;

    public HttpRequest(String rawRequest){
        String[] splittedRequest = rawRequest.split("\\s+");
        method = splittedRequest[0];
        uri = splittedRequest[1];
        System.out.println(method + " " + uri);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri.endsWith("/") ? uri.concat("index.html") : uri;
    }
}
