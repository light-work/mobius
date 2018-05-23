package com.mobius.startup;


import com.google.inject.Injector;
import com.mobius.entity.utils.EnvironmentUtils;
import com.mobius.entity.utils.EnvironmentValue;
import com.mobius.task.TaskDemo;
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


        try {
            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("environment", webConfig.getString("releaseEnvironment"));
            jobDataMap.put("startMax", 0);
            jobDataMap.put("stopMax", 0);



            JobDetail jobTaskDemo = newJob(TaskDemo.class).withIdentity("taskDemo", "groupTaskDemo")
                    .usingJobData(jobDataMap).build();
            CronTrigger triggerTaskDemo = newTrigger()
                    .withIdentity("triggerTaskDemo", "groupTaskDemo")
                    .withSchedule(cronSchedule("*/6 * * * * ?"))
                    .build();
//
//            JobDetail jobGBC = newJob(PushJobGBC.class).withIdentity("jobGBC", "groupPush")
//                    .usingJobData(jobDataMap).build();
//            CronTrigger triggerGBC = newTrigger()
//                    .withIdentity("triggerGBC", "groupPush")
//                    .withSchedule(cronSchedule("*/3 * * * * ?"))
//                    .build();
//
//            JobDetail jobBTCPrice = newJob(PushJobBTCPrice.class).withIdentity("jobBTCPrice", "group1")
//                    .usingJobData(jobDataMap).build();
//
//            CronTrigger triggerBTCPrice = newTrigger()
//                    .withIdentity("triggerBTCPrice", "group1")
//                    .withSchedule(cronSchedule("0 */1 * * * ?"))
//                    .build();
//
//
//
            scheduler.scheduleJob(jobTaskDemo, triggerTaskDemo);
//            scheduler.scheduleJob(jobGBC, triggerGBC);
//            scheduler.scheduleJob(jobBTCPrice, triggerBTCPrice);

            //scheduler.scheduleJob(jobBtcPrice, triggerBTCPrice);
            // and start it off
            scheduler.start();
            System.out.println(localIP + "启动 PushJob");

            //scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }


    public void destroyed(ServletContext servletContext) throws Exception {
        //RedisPoolProvider.destroyAll();
    }
}