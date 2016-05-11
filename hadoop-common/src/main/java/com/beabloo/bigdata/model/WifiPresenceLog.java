package com.beabloo.bigdata.model;

import com.beabloo.bigdata.cockroach.annotations.Activity;
import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.annotations.Parameter;
import com.beabloo.bigdata.cockroach.spec.Platform;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

@Activity(platform = Platform.WIFI, event = Event.PRESENCE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WifiPresenceLog extends CockroachLog {

    @Parameter
    @NotNull(message = "[device] cannot be null")
    private String device;

    @Parameter
    @NotNull(message = "[oui] cannot be null")
    private String oui;

    @Parameter
    @NotNull(message = "[sensor] cannot be null")
    private Long sensor;

    @Parameter
    @NotNull(message = "[hotspot] cannot be null")
    private Long hotspot;

    @Parameter
    @NotNull(message = "[power] cannot be null")
    private Integer power;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getSensor() {
        return sensor;
    }

    public void setSensor(Long sensor) {
        this.sensor = sensor;
    }

    public Long getHotspot() {
        return hotspot;
    }

    public void setHotspot(Long hotspot) {
        this.hotspot = hotspot;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(getOrganization()).append(fieldsTerminatedBy);
        stringBuffer.append(getStartEvent()).append(fieldsTerminatedBy);
        stringBuffer.append(getHotspot()).append(fieldsTerminatedBy);
        stringBuffer.append(getSensor()).append(fieldsTerminatedBy);
        stringBuffer.append(getDevice()).append(fieldsTerminatedBy);
        stringBuffer.append(getOui()).append(fieldsTerminatedBy);
        stringBuffer.append(getPower()).append(fieldsTerminatedBy);
        stringBuffer.append(convertHashMapToHadoopMapInStringFormat(getExtras()));

        return stringBuffer.toString();
    }

}
