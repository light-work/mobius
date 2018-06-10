package com.mobius.startup;


import com.google.inject.Injector;
import com.mobius.entity.utils.EnvironmentUtils;
import com.mobius.entity.utils.EnvironmentValue;
import com.mobius.task.*;
import com.mobius.task.daily.*;
import com.mobius.task.detail.*;
import org.apache.log4j.Logger;
import org.guiceside.commons.TimeUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;
import org.guiceside.web.listener.DefaultGuiceSideListener;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContext;

import javax.servlet.UnavailableException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * @author zhenjia <a href='mailto:zhenjiaWang@gmail.com'>email</a>
 * @version 1.0 2008-12-1
 * @since JDK1.5
 */
public class PlatformLoader {
    private static final Logger log = Logger.getLogger(PlatformLoader.class);

    private Scheduler scheduler;


    public void init(ServletContext servletContext) throws Exception {
        long tStart = System.currentTimeMillis();
        long tEnd = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("PlatformLoader done! time:" + TimeUtils.getTimeDiff(tStart, tEnd));
        }
        PropertiesConfig webConfig = new PropertiesConfig("webconfig.properties");
        String localIP = EnvironmentUtils.checkReleaseEnvironment(webConfig);

        servletContext.setAttribute("webConfig", webConfig);
        //RedisPoolProvider.init(webConfig);
        Injector injector = (Injector) servletContext
                .getAttribute(Injector.class.getName());
        if (injector == null) {
            log.error("Guice Injector not found", new UnavailableException(
                    "Guice Injector not found (did you forget to register a "
                            + DefaultGuiceSideListener.class.getSimpleName()
                            + "?)"));
            throw new Exception(
                    "Guice Injector not found (did you forget to register a "
                            + DefaultGuiceSideListener.class.getSimpleName()
                            + "?)");
        }
        injector = (Injector) servletContext.getAttribute(Injector.class
                .getName());
        if (injector == null) {
            log.error("Guice Injector not found", new UnavailableException(
                    "Guice Injector not found (did you forget to register a "
                            + DefaultGuiceSideListener.class.getSimpleName()
                            + "?)"));
        }

        String releaseEnvironment = webConfig.getString("releaseEnvironment");
        if (StringUtils.isNotBlank(releaseEnvironment)) {

        }
        EnvironmentValue.getInstance().setWebConfig(webConfig);
        String testIP = EnvironmentValue.getInstance().getValue("TEST_IP");

        int detailInteval = 30;
        if (!testIP.equals(localIP)) {
            detailInteval = 4;//正是服务器 6s
        }
        try {
            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("injector", injector);
            jobDataMap.put("localIP", localIP);
            System.out.println("detailInteval=" + detailInteval);


            String DAILY_TASK_IP = EnvironmentValue.getInstance().getValue("DAILY_TASK");
            System.out.println(DAILY_TASK_IP);
            if (StringUtils.isNotBlank(DAILY_TASK_IP)) {
                if (DAILY_TASK_IP.equals(localIP)) {
                    //daily ip task

                    /*****************Daily*******************/

//
                    JobDetail jobDailyTaskForOkex = newJob(DailyTaskForOkex.class).withIdentity("dailyTaskForOkex", "groupDailyTaskForOkex")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDailyTaskForOkex = newTrigger()
                            .withIdentity("triggerDailyTaskForOkex", "groupDailyTaskForOkex")
                            .withSchedule(cronSchedule("0 30 0 * * ?"))//每天的 0点到0点5分触发
                            .build();

                    JobDetail jobDailyTaskForHuobi = newJob(DailyTaskForHuobi.class).withIdentity("dailyTaskForHuobi", "groupDailyTaskForHuobi")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDailyTaskForHuobi = newTrigger()
                            .withIdentity("triggerDailyTaskForHuobi", "groupDailyTaskForHuobi")
                            .withSchedule(cronSchedule("0 40 0 * * ?"))//每天的 0点到0点10分触发
                            .build();

                    JobDetail jobDailyTaskForBitmex = newJob(DailyTaskForBitmex.class).withIdentity("dailyTaskForBitmex", "groupDailyTaskForBitmex")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDailyTaskForBitmex = newTrigger()
                            .withIdentity("triggerDailyTaskForBitmex", "groupDailyTaskForBitmex")
                            .withSchedule(cronSchedule("0 32 0 * * ?"))//每天的 0点到0点6分触发
                            .build();


                    JobDetail jobDailyTaskForBinance = newJob(DailyTaskForBinance.class).withIdentity("dailyTaskForBinance", "groupDailyTaskForBinance")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDailyTaskForBinance = newTrigger()
                            .withIdentity("triggerDailyTaskForBinance", "groupDailyTaskForBinance")
                            .withSchedule(cronSchedule("0 45 0 * * ?"))//每天的 0点到0点15分触发
                            .build();


                    JobDetail jobDailyTaskForBitfinex = newJob(DailyTaskForBitfinex.class).withIdentity("dailyTaskForBitfinex", "groupDailyTaskForBitfinex")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDailyTaskForBitfinex = newTrigger()
                            .withIdentity("triggerDailyTaskForBitfinex", "groupDailyTaskForBitfinex")
                            .withSchedule(cronSchedule("0 55 0 * * ?"))//每天的 0点到0点25分触发
                            .build();


                    JobDetail jobCapitalizationTask = newJob(CapitalizationTask.class).withIdentity("dailyCapitalizationTask", "groupCapitalizationTask")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerCapitalizationTask = newTrigger()
                            .withIdentity("triggerCapitalizationTask", "groupCapitalizationTask")
                            .withSchedule(cronSchedule("0 0 0 * * ?"))//每天的 0点到0点10分每分触发
                            .build();


                    scheduler.scheduleJob(jobDailyTaskForOkex, triggerDailyTaskForOkex);
                    scheduler.scheduleJob(jobDailyTaskForBinance, triggerDailyTaskForBinance);
                    scheduler.scheduleJob(jobDailyTaskForBitfinex, triggerDailyTaskForBitfinex);
                    scheduler.scheduleJob(jobDailyTaskForBitmex, triggerDailyTaskForBitmex);
                    scheduler.scheduleJob(jobDailyTaskForHuobi, triggerDailyTaskForHuobi);
                    scheduler.scheduleJob(jobCapitalizationTask, triggerCapitalizationTask);


                    JobDetail jobDetailTaskForBinanceUsdt = newJob(DetailTaskForBinanceUsdt.class).withIdentity("detailTaskForBinanceUsdt", "groupDetailTaskForBinanceUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForBinanceUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForBinanceUsdt", "groupDetailTaskForBinanceUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForBitfinexUsdt = newJob(DetailTaskForBitfinexUsdt.class).withIdentity("detailTaskForBitfinexUsdt", "groupDetailTaskForBitfinexUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForBitfinexUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForBitfinexUsdt", "groupDetailTaskForBitfinexUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    JobDetail jobDetailTaskForOKexSpotUsdt = newJob(DetailTaskForOkexSpotUsdt.class).withIdentity("detailTaskForOKexSpotUsdt", "groupDetailTaskForOKexSpotUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForOKexSpotUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForOKexSpotUsdt", "groupDetailTaskForOKexSpotUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForHuobiUsdt = newJob(DetailTaskForHuobiUsdt.class).withIdentity("detailTaskForHuobiUsdt", "groupDetailTaskForHuobiUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForHuobiUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForHuobiUsdt", "groupDetailTaskForHuobiUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    scheduler.scheduleJob(jobDetailTaskForHuobiUsdt, triggerDetailTaskForHuobiUsdt);
                    scheduler.scheduleJob(jobDetailTaskForBinanceUsdt, triggerDetailTaskForBinanceUsdt);
                    scheduler.scheduleJob(jobDetailTaskForBitfinexUsdt, triggerDetailTaskForBitfinexUsdt);
                    scheduler.scheduleJob(jobDetailTaskForOKexSpotUsdt, triggerDetailTaskForOKexSpotUsdt);

                } else {
                    //daily
                    JobDetail jobDetailTaskForBinanceUsdt = newJob(DetailTaskForBinanceUsdt.class).withIdentity("detailTaskForBinanceUsdt", "groupDetailTaskForBinanceUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForBinanceUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForBinanceUsdt", "groupDetailTaskForBinanceUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForBinanceBtc = newJob(DetailTaskForBinanceBtc.class).withIdentity("detailTaskForBinanceBtc", "groupDetailTaskForBinanceBtc")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForBinanceBtc = newTrigger()
                            .withIdentity("triggerDetailTaskForBinanceBtc", "groupDetailTaskForBinanceBtc")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForBinanceEth = newJob(DetailTaskForBinanceEth.class).withIdentity("detailTaskForBinanceEth", "groupDetailTaskForBinanceEth")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForBinanceEth = newTrigger()
                            .withIdentity("triggerDetailTaskForBinanceEth", "groupDetailTaskForBinanceEth")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    //okex
                    JobDetail jobDetailTaskForOKexSpotUsdt = newJob(DetailTaskForOkexSpotUsdt.class).withIdentity("detailTaskForOKexSpotUsdt", "groupDetailTaskForOKexSpotUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForOKexSpotUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForOKexSpotUsdt", "groupDetailTaskForOKexSpotUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    JobDetail jobDetailTaskForOKexSpotBtc = newJob(DetailTaskForOkexSpotBtc.class).withIdentity("detailTaskForOKexSpotBtc", "groupDetailTaskForOKexSpotBtc")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForOKexSpotBtc = newTrigger()
                            .withIdentity("triggerDetailTaskForOKexSpotBtc", "groupDetailTaskForOKexSpotBtc")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForOKexSpotEth = newJob(DetailTaskForOkexSpotEth.class).withIdentity("detailTaskForOKexEth", "groupDetailTaskForOKexSpotEth")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForOKexSpotEth = newTrigger()
                            .withIdentity("triggerDetailTaskForOKexSpotEth", "groupDetailTaskForOKexSpotEth")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForOKexFuturesUsdt = newJob(DetailTaskForOkexFuturesUsdt.class).withIdentity("detailTaskForOKexFuturesUsdt", "groupDetailTaskForOKexFuturesUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForOKexFuturesUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForOKexFuturesUsdt", "groupDetailTaskForOKexFuturesUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    //huobi
                    JobDetail jobDetailTaskForHuobiUsdt = newJob(DetailTaskForHuobiUsdt.class).withIdentity("detailTaskForHuobiUsdt", "groupDetailTaskForHuobiUsdt")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForHuobiUsdt = newTrigger()
                            .withIdentity("triggerDetailTaskForHuobiUsdt", "groupDetailTaskForHuobiUsdt")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    JobDetail jobDetailTaskForHuobiBtc = newJob(DetailTaskForHuobiBtc.class).withIdentity("detailTaskForHuobiBtc", "groupDetailTaskForHuobiBtc")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForHuobiBtc = newTrigger()
                            .withIdentity("triggerDetailTaskForHuobiBtc", "groupDetailTaskForHuobiBtc")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();

                    JobDetail jobDetailTaskForHuobiEth = newJob(DetailTaskForHuobiEth.class).withIdentity("detailTaskForHuobiEth", "groupDetailTaskForHuobiEth")
                            .usingJobData(jobDataMap).build();
                    CronTrigger triggerDetailTaskForHuobiEth = newTrigger()
                            .withIdentity("triggerDetailTaskForHuobiEth", "groupDetailTaskForHuobiEth")
                            .withSchedule(cronSchedule("0/" + detailInteval + " * * * * ?"))//每6秒触发
                            .build();


                    scheduler.scheduleJob(jobDetailTaskForHuobiUsdt, triggerDetailTaskForHuobiUsdt);
                    scheduler.scheduleJob(jobDetailTaskForHuobiBtc, triggerDetailTaskForHuobiBtc);
                    scheduler.scheduleJob(jobDetailTaskForHuobiEth, triggerDetailTaskForHuobiEth);


                    scheduler.scheduleJob(jobDetailTaskForOKexFuturesUsdt, triggerDetailTaskForOKexFuturesUsdt);
                    scheduler.scheduleJob(jobDetailTaskForOKexSpotUsdt, triggerDetailTaskForOKexSpotUsdt);
                    scheduler.scheduleJob(jobDetailTaskForOKexSpotBtc, triggerDetailTaskForOKexSpotBtc);
                    scheduler.scheduleJob(jobDetailTaskForOKexSpotEth, triggerDetailTaskForOKexSpotEth);


                    scheduler.scheduleJob(jobDetailTaskForBinanceUsdt, triggerDetailTaskForBinanceUsdt);
                    scheduler.scheduleJob(jobDetailTaskForBinanceBtc, triggerDetailTaskForBinanceBtc);
                    scheduler.scheduleJob(jobDetailTaskForBinanceEth, triggerDetailTaskForBinanceEth);
                }
            }

//            JobDetail jobBTCPrice = newJob(PushJobBTCPrice.class).withIdentity("jobBTCPrice", "group1")
//                    .usingJobData(jobDataMap).build();
//
//            CronTrigger triggerBTCPrice = newTrigger()
//                    .withIdentity("triggerBTCPrice", "group1")
//                    .withSchedule(cronSchedule("0 */1 * * * ?"))
//                    .build();
//
//
////
//            scheduler.scheduleJob(jobDetailTaskForBinanceUsdt, triggerDetailTaskForBinanceUsdt);
//            scheduler.scheduleJob(jobDetailTaskForBinanceBtc, triggerDetailTaskForBinanceBtc);
//            scheduler.scheduleJob(jobDetailTaskForBinanceEth, triggerDetailTaskForBinanceEth);


//
//            scheduler.scheduleJob(jobDetailTaskForHuobiUsdt, triggerDetailTaskForHuobiUsdt);
//            scheduler.scheduleJob(jobDetailTaskForHuobiBtc, triggerDetailTaskForHuobiBtc);
//            scheduler.scheduleJob(jobDetailTaskForHuobiEth, triggerDetailTaskForHuobiEth);

//
//            scheduler.scheduleJob(jobDetailTaskForOKexFuturesUsdt, triggerDetailTaskForOKexFuturesUsdt);
//            scheduler.scheduleJob(jobDetailTaskForOKexSpotUsdt, triggerDetailTaskForOKexSpotUsdt);
//            scheduler.scheduleJob(jobDetailTaskForOKexSpotBtc, triggerDetailTaskForOKexSpotBtc);
//            scheduler.scheduleJob(jobDetailTaskForOKexSpotEth, triggerDetailTaskForOKexSpotEth);


//            scheduler.scheduleJob(jobBTCPrice, triggerBTCPrice);

            //scheduler.scheduleJob(jobBtcPrice, triggerBTCPrice);
            // and start it off
            scheduler.start();
            System.out.println(localIP + "启动 task");

            //scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }


    public void destroyed(ServletContext servletContext) throws Exception {
        //RedisPoolProvider.destroyAll();
        scheduler.shutdown();
    }
}