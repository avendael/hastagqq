package com.hastagqq.app.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author avendael
 */
public class BasicApiResponse {
    public static final String SUCCESS = "00000";
    public static final String FAIL = "00001";

    @Expose
    @SerializedName("response")
    private String responseCode;

    @Expose
    private String message;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
