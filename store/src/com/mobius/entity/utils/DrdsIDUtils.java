package com.mobius.entity.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhenjiaWang on 16/7/14.
 */
public class DrdsIDUtils {


    public static Map<DrdsTable, IdWorker> idWorkerMap = new HashMap<>();

    static {
        IdWorker sysIdWorker = new IdWorker(1);
        idWorkerMap.put(DrdsTable.SYS, sysIdWorker);

        IdWorker entrustIdWorker = new IdWorker(2);
        idWorkerMap.put(DrdsTable.SPOT, entrustIdWorker);

        IdWorker transferIdWorker = new IdWorker(3);
        idWorkerMap.put(DrdsTable.FUTURES, transferIdWorker);


        IdWorker calIdWorker = new IdWorker(4);
        idWorkerMap.put(DrdsTable.CAL, calIdWorker);


    }

    public static Long getID(DrdsTable drdsTable) {
        return idWorkerMap.get(drdsTable).getId();
    }

    public static void main(String[] args) {
    }
}
