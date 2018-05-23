package com.mobius.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * @author zhenjia <a href='mailto:zhenjiaWang@gmail.com'>email</a>
 * @version 1.0 2008-12-1
 * @since JDK1.5
 */
public class PlatformLoaderListener implements ServletContextListener {
    private PlatformLoader platformLoader;

    public void contextDestroyed(ServletContextEvent event) {
        try {
            getPlatformLoader().destroyed(event.getServletContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        this.platformLoader = createPlatformLoader();
        try {
            this.platformLoader.init(event.getServletContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected PlatformLoader createPlatformLoader() {
        return new PlatformLoader();
    }

    public PlatformLoader getPlatformLoader() {
        return platformLoader;
    }

}