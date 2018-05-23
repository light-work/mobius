package com.mobius.filter;

import com.mobius.common.BaseFilter;
import com.mobius.entity.utils.EnvironmentUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Created by gbcp on 2016/12/28.
 */
@PreMatching
@Priority(value = 3)
@Provider
public class IdempotenceRequestFilter extends BaseFilter implements
        ContainerRequestFilter {
    private PropertiesConfig webConfig = null;
    private String releaseEnvironment = null;
    private String webIP = null;

    public IdempotenceRequestFilter() {
        System.out.println("IdempotenceRequestFilter initialization");
        webConfig = new PropertiesConfig("webconfig.properties");
        EnvironmentUtils.checkReleaseEnvironment(webConfig);
        releaseEnvironment = webConfig.getString("releaseEnvironment");
        if (StringUtils.isNotBlank(releaseEnvironment)) {
            webIP = webConfig.getString(releaseEnvironment + "_WEB_IP");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!requestContext.getMethod().toUpperCase().equals("OPTIONS")) {
            String path = requestContext.getUriInfo().getPath();
            if (StringUtils.isNotBlank(path)) {
                if (!path.startsWith("public") && !path.startsWith("trust")) {
                    String sessionID = requestContext.getProperty("userSessionId").toString();
                    long timeMillis = System.currentTimeMillis();
//                    if (StringUtils.isNotBlank(sessionID)) {
//                        if (requestContext.getMethod().toUpperCase().equals("POST")) {
//                            boolean flag = ApiTotalUtils.idempotence(path, sessionID, timeMillis);
//                            if (flag) {
//                                ApiTotalUtils.call(path, sessionID, timeMillis);
//                                requestContext.abortWith(responseBuild(401));
//                            } else {
//                                ApiTotalUtils.call(path, sessionID, timeMillis);
//                            }
//                        } else if (requestContext.getMethod().toUpperCase().equals("GET")) {
//                            ApiTotalUtils.call(path, sessionID, timeMillis);
//                        }
//                    } else {
//                        requestContext.removeProperty("userSessionId");
//                        requestContext.abortWith(responseBuild(401));
//                    }
                }
            }
        }
    }
}
