package com.beabloo.bigdata.model;

import com.beabloo.bigdata.cockroach.aspects.Activity;
import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.aspects.Parameter;
import com.beabloo.bigdata.cockroach.spec.Platform;

@Activity(platform = Platform.WIFI, event = Event.PRESENCE)
public class WifiPresence extends CockroachLog {

    @Parameter
    private Long orgnizationId;

    @Parameter
    private String device;

    @Parameter
    private Long sensorId;

    @Parameter
    private Long hotspotId;

    @Parameter
    private Double txPower;

    @Parameter
    private String oui;

    private Long recordLastUpdate;

    public Long getOrgnizationId() {
        return orgnizationId;
    }

    public void setOrgnizationId(Long orgnizationId) {
        this.orgnizationId = orgnizationId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(Long hotspotId) {
        this.hotspotId = hotspotId;
    }

    public Double getTxPower() {
        return txPower;
    }

    public void setTxPower(Double txPower) {
        this.txPower = txPower;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public Long getRecordLastUpdate() {
        return recordLastUpdate;
    }

    public void setRecordLastUpdate(Long recordLastUpdate) {
        this.recordLastUpdate = recordLastUpdate;
    }

}
