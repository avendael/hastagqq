package com.hastagqq.app.model;

import com.google.gson.annotations.Expose;

/**
 * @author avendael
 */
public class DeviceInfo {
    public DeviceInfo () {}

    public DeviceInfo(String gcmId, String location) {
        this.gcmId = gcmId;
        this.location = location;
    }

    @Expose
    private String gcmId;

    @Expose
    private String location;

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }
}
