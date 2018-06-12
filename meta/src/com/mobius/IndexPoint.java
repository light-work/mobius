package com.mobius;

import org.guiceside.support.redis.RedisPoolProvider;
import org.guiceside.support.redis.RedisStoreUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class IndexPoint {


    private static class SingletonHolder {
        private static final IndexPoint INSTANCE = new IndexPoint();
    }


    private IndexPoint() {
    }

    public static final IndexPoint getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Double getIndex() {
        Double index = null;
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                index = RedisStoreUtils.getDouble(jedis, "MOBIUS_INDEX", 8);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return index;
    }

    public void setIndex(Double index) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                RedisStoreUtils.set(jedis, "MOBIUS_INDEX", index + "");
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }
}
