package com.sncf.itnovem.dotandroidapplication.services;


import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.*;

/**
 * Created by Save92 on 12/05/16.
 */
public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Retrofit retrofit = ServiceGenerator.createService(Retrofit.class,API.RESTAPIURL);
            Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);

            APIError error;

            try {
                error = converter.convert(response.errorBody());
            } catch (IOException e) {
                return new APIError();
            }

            return error;
        }
}
