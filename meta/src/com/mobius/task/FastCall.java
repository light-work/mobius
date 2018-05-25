package com.mobius.task;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastCall {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void send(final String url, final Map<String, String> mapParams) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    //todo 调用api 保存 都在这里写
                } catch (Exception e) {

                }
            }
        });
    }


}
