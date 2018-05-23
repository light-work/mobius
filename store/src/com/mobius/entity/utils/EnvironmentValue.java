package com.mobius.entity.utils;


import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;

/**
 * Created by zhenjiawang on 2016/12/29.
 */
public final class EnvironmentValue {

    private PropertiesConfig webConfig;

    private static class SingletonHolder {
        private static final EnvironmentValue INSTANCE = new EnvironmentValue();
    }


    private EnvironmentValue() {
    }

    public static final EnvironmentValue getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void setWebConfig(PropertiesConfig webConfig) {
        this.webConfig = webConfig;
    }


    public String getValue(String key) {
        String v = null;
        if (webConfig != null) {
            String releaseEnvironment = webConfig.getString("releaseEnvironment");
            if (StringUtils.isNotBlank(releaseEnvironment)) {
                v = webConfig.getString(releaseEnvironment + "_" + key);
            }
        }
        return v;
    }
}
