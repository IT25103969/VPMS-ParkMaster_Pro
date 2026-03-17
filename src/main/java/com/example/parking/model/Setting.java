package com.example.parking.model;

import java.io.Serializable;

public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String configKey;
    private String configValue;

    public Setting() {}

    public Setting(Long id, String configKey, String configValue) {
        this.id = id;
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
}
