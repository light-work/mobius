package com.mobius.entity.utils;

import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;

import java.net.InetAddress;

/**
 * Created by zhenjiawang on 2017/1/15.
 */
public class EnvironmentUtils {

    public static String checkReleaseEnvironment(PropertiesConfig webConfig){
        InetAddress ia = null;
        String localIP = null;
        try {
            ia = ia.getLocalHost();
            localIP = ia.getHostAddress();
            System.out.println("本机名称是：" + localIP);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(localIP)){
            if (localIP.startsWith("172.28")) {
                webConfig.setString("releaseEnvironment", "LOCAL");
            }else{
                boolean disFlag=false;
                String disIPS=  webConfig.getString("DIS_IP");
                if(StringUtils.isNotBlank(disIPS)){
                    if(localIP.startsWith(disIPS)){
                        disFlag=true;
                    }
                }
                if(disFlag){
                    webConfig.setString("releaseEnvironment","DIS");
                }
            }
            System.out.println("检测到ip为：" + localIP + " 是" + webConfig.getString("releaseEnvironment") + "环境 已经自动变动");
        }
        return localIP;
    }
}
