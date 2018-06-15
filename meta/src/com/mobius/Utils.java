package com.mobius;

import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotSymbol;
import ognl.NoSuchPropertyException;
import org.guiceside.commons.lang.BeanUtils;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.Tracker;
import org.guiceside.support.redis.RedisPoolProvider;
import org.guiceside.support.redis.RedisStoreUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils {

    /**
     * 设置样本开市权重
     * @param spotSymbol
     * @param cud
     * @param weight
     */
    public static void setSymbolWeight(SpotSymbol spotSymbol,Date cud, Double weight) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                weight=NumberUtils.multiply(weight,1,8);
                RedisStoreUtils.hset(jedis, "SPOT_SYMBOL_WEIGHT", spotSymbol.getId() + "_"+DateFormatUtil.format(cud,DateFormatUtil.YEAR_MONTH_DAY_PATTERN), weight + "");
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 获取样本开市权重
     * @param spotSymbol
     * @param cud
     * @return
     */
    public static Double getSymbolWeight(SpotSymbol spotSymbol,Date cud) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        Double price = null;
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price = RedisStoreUtils.hgetDouble(jedis, "SPOT_SYMBOL_WEIGHT", spotSymbol.getId() + "_"+DateFormatUtil.format(cud,DateFormatUtil.YEAR_MONTH_DAY_PATTERN), 8);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return price;
    }

    /**
     * 设置指数
     *
     * @param cud
     * @param point
     */
    public static void setPointByDate(Date cud, Double point) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                point = NumberUtils.multiply(point, 1, 8);
                RedisStoreUtils.hset(jedis, "INDEX_POINT", DateFormatUtil.format(cud, DateFormatUtil.YEAR_MONTH_DAY_PATTERN), point + "");
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 获取指数
     *
     * @param cud
     */
    public static Double getPointByDate(Date cud) {
        Double price = null;
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price = RedisStoreUtils.hgetDouble(jedis, "INDEX_POINT", DateFormatUtil.format(cud, DateFormatUtil.YEAR_MONTH_DAY_PATTERN), 8);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return price;
    }






    /**
     * 设置样本日频 收盘价格
     *
     * @param spotSymbol
     * @param price
     */
    public static void setDailySymbolPrice(SpotSymbol spotSymbol, Double price, Date dateTime) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price = NumberUtils.multiply(price, 1, 8);
                RedisStoreUtils.hset(jedis, "SPOT_SYMBOL_PRICE" +DateFormatUtil.format(dateTime, DateFormatUtil.YEAR_MONTH_DAY_PATTERN), spotSymbol.getId() + "_", price + "");
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 获取样本日频 收盘价格
     *
     * @param spotSymbol
     * @return
     */
    public static Double getDailySymbolPrice(SpotSymbol spotSymbol, Date dateTime) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        Double price = null;
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price = RedisStoreUtils.hgetDouble(jedis, "SPOT_SYMBOL_PRICE" +DateFormatUtil.format(dateTime, DateFormatUtil.YEAR_MONTH_DAY_PATTERN), spotSymbol.getId() + "_" , 8);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return price;
    }

    /**
     * 设置样本合约 高频当前价格
     * @param calSampleSpotSymbolWeight
     * @param price
     */
    public static void setWeightSymbolPrice(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight, Double price) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price=NumberUtils.multiply(price,1,8);
                RedisStoreUtils.hset(jedis, "WEIGHT_SYMBOL_PRICE", calSampleSpotSymbolWeight.getId() + "", price + "");
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 获取样本合约 高频当前价格
     * @param calSampleSpotSymbolWeight
     * @return
     */
    public static Double getWeightSymbolPrice(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) {
        JedisPool pool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
        Double price = null;
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                price = RedisStoreUtils.hgetDouble(jedis, "WEIGHT_SYMBOL_PRICE", calSampleSpotSymbolWeight.getId() + "", 8);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return price;
    }


    public static Date parseDateForZ(String timeStr) {
        timeStr = timeStr.replace("Z", " UTC");//UTC是本地时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date d = null;
        try {
            d = format.parse(timeStr);
            d = DateFormatUtil.addDay(d, -1);
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

    public static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }


    public static double median(List<Double> data) {
        double mid = 0;
        Collections.sort(data);
        int len = data.size();
        //System.out.println(data.toString());
        if (len % 2 == 0) mid = (data.get(len / 2 - 1) + data.get(len / 2)) / 2;
        else mid = data.get(len / 2);
//        System.out.println("数组data的中位数为："+mid);
        return mid;
    }


    public static void bind(Object entity, String by) throws Exception {
        if (entity instanceof Tracker) {
            BeanUtils.setValue(entity, "created", DateFormatUtil.getCurrentDate(true));
            BeanUtils.setValue(entity, "updated", DateFormatUtil.getCurrentDate(true));
            BeanUtils.setValue(entity, "createdBy", StringUtils.defaultIfEmpty(by, "sys"));
            BeanUtils.setValue(entity, "updatedBy", StringUtils.defaultIfEmpty(by, "sys"));
        }
        try {
            String useYn = BeanUtils.getValue(entity, "useYn", String.class);
            if (StringUtils.isBlank(useYn)) {
                BeanUtils.setValue(entity, "useYn", "Y");
            }
        } catch (NoSuchPropertyException e) {
            BeanUtils.setValue(entity, "useYn", "Y");
        }
    }
}

