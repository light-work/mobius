package com.mobius.utils;

import org.guiceside.commons.OKHttpUtil;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastCallbackSend {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);


    public static void send(final String url, final Map<String, String> mapParams) {
        executorService.execute(new Runnable() {
            public void run() {
                try{
                    OKHttpUtil.post(url, mapParams);
                    System.out.println("call ****>"+url);
                }catch (Exception e){

                }
            }
        });
    }


}
