package com.mobius.utils;

import io.goeasy.GoEasy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastGoEasy {

    private static ExecutorService executorService = Executors.newFixedThreadPool(12);
    private static GoEasy goEasy = new GoEasy("BC-30ba7f4f34f3474bae14259b8fe897d6");


    public static void publish(final String channel, final String content) {
        executorService.execute(new Runnable() {
            public void run() {
                goEasy.publish(channel, content);
            }
        });
    }


}
