package com.mobius.filter;

import com.mobius.common.BaseFilter;
import com.mobius.entity.utils.EnvironmentUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;
import org.guiceside.support.redis.RedisPoolProvider;
import org.guiceside.support.redis.session.RedisSessionUtils;
import redis.clients.jedis.JedisPool;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * Created by gbcp on 2016/12/28.
 */
@PreMatching
@Priority(value = 4)
@Provider
public class PostTokenRequestFilter extends BaseFilter implements
        ContainerRequestFilter {
    private PropertiesConfig webConfig = null;
    private String releaseEnvironment = null;
    private String webIP = null;

    public PostTokenRequestFilter() {
        System.out.println("PostTokenRequestFilter initialization");
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
                if (!path.startsWith("public") && !path.startsWith("trust") ) {
                    if (requestContext.getMethod().toUpperCase().equals("POST")) {
                        String sessionID = requestContext.getProperty("userSessionId").toString();
                        String authorizationToken = requestContext.getHeaderString("Authorization");
                        if (StringUtils.isNotBlank(authorizationToken)) {
                            String[] auToken = authorizationToken.split("_");
                            if (auToken != null && auToken.length == 3) {
                                String tokenUUID = auToken[0];
                                String token = auToken[1];
                                String tokenUserId = auToken[2];
                                if (StringUtils.isNotBlank(sessionID) && StringUtils.isNotBlank(tokenUUID) && StringUtils.isNotBlank(token)&&StringUtils.isNotBlank(tokenUserId)) {
                                    JedisPool jedisPool = RedisPoolProvider.getPool("REDIS_SESSION");
                                    if (jedisPool != null) {
                                        String tokenSessionUserId = RedisSessionUtils.getAttr(jedisPool, sessionID, "userId");
                                        if (StringUtils.isNotBlank(tokenSessionUserId)) {
                                            if(tokenSessionUserId.equals(tokenUserId)){
                                                String tokenValue = RedisSessionUtils.getAttr(jedisPool, sessionID, tokenUUID);
                                                if (StringUtils.isNotBlank(tokenValue)) {
                                                    if (!tokenValue.equals(token)) {
                                                        requestContext.abortWith(responseBuild(401));
                                                    } else {
                                                        RedisSessionUtils.removeAttr(jedisPool, sessionID, tokenUUID);
                                                    }
                                                }
                                            }else{
                                                requestContext.abortWith(responseBuild(416));
                                            }
                                        }else{
                                            requestContext.abortWith(responseBuild(416));
                                        }
                                    }
                                } else {
                                    requestContext.abortWith(responseBuild(401));
                                }
                            } else {
                                requestContext.abortWith(responseBuild(401));
                            }
                        } else {
                            requestContext.abortWith(responseBuild(401));
                        }
                        requestContext.removeProperty("userSessionId");
                    }
                }
            }
        }
    }
}
