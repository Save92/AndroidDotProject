package com.sncf.itnovem.dotandroidapplication.services;

/**
 * Created by Save92 on 12/05/16.
 */
public class APIError {

    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
