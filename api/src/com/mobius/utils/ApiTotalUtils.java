package com.mobius.utils;

import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.support.redis.RedisPoolProvider;
import org.guiceside.support.redis.RedisStoreUtils;
import org.guiceside.support.redis.RedisTxStoreUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Date;

/**
 * Created by gbcp on 15/8/8.
 */
public class ApiTotalUtils {


    public static final String API_CALL_TOTAL_ALL = "API_CALL_TOTAL_ALL";
    public static final String API_CALL_TOTAL_DATE = "API_CALL_TOTAL_DATE";

    public static boolean idempotence(String path, String who, long timeMillis) {
        boolean flag = false;
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                Date currentDayDate = new Date(timeMillis);
                jedis = pool.getResource();
                String whoDateKey = who + "_" + DateFormatUtil.format(currentDayDate, "yyyyMMddHHmm");
                String timeMillisValue = DateFormatUtil.format(currentDayDate, DateFormatUtil.YMDHMS_TIMESTAMP);
                long idempotenceValue = RedisStoreUtils.hgetLong(jedis, whoDateKey, path + "_" + timeMillisValue);
                if (idempotenceValue > 1) {
                    flag = true;
                }
                if (flag) {
                    currentDayDate = DateFormatUtil.addSecond(currentDayDate, -1);
                    timeMillisValue = DateFormatUtil.format(currentDayDate, DateFormatUtil.YMDHMS_TIMESTAMP);
                    idempotenceValue = RedisStoreUtils.hgetLong(jedis, whoDateKey, path + "_" + timeMillisValue);
                    if (idempotenceValue > 1) {
                        flag = true;
                    }
                }

            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return flag;
    }

    public static void call(String path, String who, long timeMillis) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                Date currentDayDate = new Date(timeMillis);
                String currentDayDateKey = DateFormatUtil.format(currentDayDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                String apiCallTotalDateKey = (API_CALL_TOTAL_DATE + "_" + currentDayDateKey);
                jedis = pool.getResource();
                RedisStoreUtils.hincrByFloat(jedis, API_CALL_TOTAL_ALL, path, 1);
                RedisStoreUtils.hincrByFloat(jedis, apiCallTotalDateKey, path, 1);
                if (!who.equals("GUEST")) {
                    String whoDateKey = who + "_" + DateFormatUtil.format(currentDayDate, "yyyyMMddHHmm");
                    String timeMillisValue = DateFormatUtil.format(currentDayDate, DateFormatUtil.YMDHMS_TIMESTAMP);
                    RedisStoreUtils.hincrByFloat(jedis, whoDateKey, path + "_" + timeMillisValue, 1);
                    RedisStoreUtils.expire(jedis, whoDateKey, 60 * 5);
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }
}
