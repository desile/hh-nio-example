package ru.hh.school.http;

public enum HttpResponseCode {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED"),
    BAD_REQUEST(400, "BAD REQUEST");

    private int code;
    private String name;

    HttpResponseCode(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
