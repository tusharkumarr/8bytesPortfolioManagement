package com.portfolio.management.model;

public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public GenericResponse() {}

    public GenericResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> GenericResponse<T> success(String message, T data) {
        return new GenericResponse<>(true, message, data);
    }

    public static <T> GenericResponse<T> failure(String message) {
        return new GenericResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
