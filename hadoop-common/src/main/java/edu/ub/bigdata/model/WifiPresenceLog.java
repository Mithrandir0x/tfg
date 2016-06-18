package edu.ub.bigdata.model;

import edu.ub.bigdata.yaelp.annotations.Activity;
import edu.ub.bigdata.yaelp.model.YaelpLog;
import edu.ub.bigdata.yaelp.spec.Trigger;
import edu.ub.bigdata.yaelp.annotations.Property;
import edu.ub.bigdata.yaelp.spec.Environment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Activity(environment = Environment.WIFI, trigger = Trigger.PRESENCE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WifiPresenceLog extends YaelpLog {

    @Property
    @NotNull(message = "[device] cannot be null")
    private String device;

    @Property
    @NotNull(message = "[oui] cannot be null")
    @Pattern(regexp="^[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}$",
        message="[oui] value is not acceptable")
    private String oui;

    @Property
    @NotNull(message = "[sensor] cannot be null")
    private Long sensor;

    @Property
    @NotNull(message = "[hotspot] cannot be null")
    private Long hotspot;

    @Property
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
