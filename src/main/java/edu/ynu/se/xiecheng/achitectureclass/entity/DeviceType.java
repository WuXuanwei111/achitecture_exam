package edu.ynu.se.xiecheng.achitectureclass.entity;

import lombok.Getter;

@Getter
public enum DeviceType {
    SOIL_MOISTURE("327061375295689615", "土壤含水率", "%"),
    SOIL_TEMPERATURE("327061375295689614", "土壤温度", "℃"),
    SOIL_CONDUCTIVITY("327061375295689605", "土壤电导率", "us/cm"),
    SOIL_PH("327061375295689606", "土壤PH值", "pH"),
    SOIL_NITROGEN("327061375295689607", "土壤氮含量", "mg/kg"),
    SOIL_PHOSPHORUS("327061375295689608", "土壤磷含量", "mg/kg"),
    SOIL_POTASSIUM("327061375295689609", "土壤钾含量", "mg/kg"),
    AIR_TEMPERATURE("327061375295689611", "空气温度", "℃"),
    AIR_HUMIDITY("327061375295689612", "空气相对湿度", "%RH"),
    LIGHT("327061375295689613", "光照度", "Lux"),
    CO2("327061375295689610", "二氧化碳", "ppm"),
    CAMERA("CAMERA_001", "监控摄像头", null);
    
    private final String deviceSerial;
    private final String displayName;
    private final String unit;
    
    DeviceType(String deviceSerial, String displayName, String unit) {
        this.deviceSerial = deviceSerial;
        this.displayName = displayName;
        this.unit = unit;
    }

    public static DeviceType fromSerial(String serial) {
        for (DeviceType type : DeviceType.values()) {
            if (type.getDeviceSerial().equals(serial)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device serial: " + serial);
    }
} 