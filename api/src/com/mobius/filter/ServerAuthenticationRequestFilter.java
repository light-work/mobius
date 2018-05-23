package com.mobius.filter;

import com.mobius.common.BaseFilter;
import com.mobius.entity.utils.EnvironmentUtils;
import com.mobius.utils.ApiTotalUtils;
import com.mobius.utils.JwtKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.properties.PropertiesConfig;
import org.guiceside.support.redis.RedisPoolProvider;
import org.guiceside.support.redis.RedisStoreUtils;
import org.guiceside.support.redis.session.RedisSessionException;
import org.guiceside.support.redis.session.RedisUserInfo;
import org.guiceside.support.redis.session.RedisUserSession;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;
import java.security.Key;
import java.util.Date;

/**
 * Created by gbcp on 2016/12/28.
 */
@PreMatching
@Priority(value = 2)
@Provider
public class ServerAuthenticationRequestFilter extends BaseFilter implements
        ContainerRequestFilter {
    private PropertiesConfig webConfig = null;
    private String releaseEnvironment = null;
    private String webIP = null;

    public ServerAuthenticationRequestFilter() {
        System.out.println("ServerAuthenticationRequestFilter initialization");
        webConfig = new PropertiesConfig("webconfig.properties");
        EnvironmentUtils.checkReleaseEnvironment(webConfig);
        releaseEnvironment = webConfig.getString("releaseEnvironment");
        if (StringUtils.isNotBlank(releaseEnvironment)) {
            webIP = webConfig.getString(releaseEnvironment + "_WEB_IP");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (StringUtils.isNoneBlank(path)) {
            if (!path.startsWith("public") && !path.startsWith("trust")) {
                String sessionJWT = null;
                if (webIP.equals("*")) {
                   /**/
                    JedisPool jedisPool = RedisPoolProvider.getPool(RedisPoolProvider.REDIS_COMMON);
                    if (jedisPool != null) {
                        Jedis jedis = null;
                        try {
                            String key = "sessionJWT_TEST";
                            jedis = jedisPool.getResource();
                            jedis.select(1);
                            sessionJWT = RedisStoreUtils.getString(jedis, key);
                        } finally {
                            if (jedis != null) {
                                jedis.close();
                            }

                        }
                    }
                    if (StringUtils.isBlank(sessionJWT)) {
                        requestContext.abortWith(responseBuild(400));
                        return;
                    }
                } else {
                    if (!requestContext.getMethod().toUpperCase().equals("OPTIONS")) {
                        Cookie cookie = requestContext.getCookies().get("sessionJWT");
                        if (cookie == null) {
                            requestContext.abortWith(responseBuild(400));
                            return;
                        }
                        sessionJWT = cookie.getValue();
                    }
                }
                if (StringUtils.isNotBlank(sessionJWT)) {
                    String sessionID = null;
                    Key key = JwtKey.getInstance().getKey();
                    try {
                        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(sessionJWT).getBody();
                        if (claims != null) {
                            Date expirationDate = claims.getExpiration();
                            Date curDate = DateFormatUtil.getCurrentDate(true);
                            if (curDate.getTime() <= expirationDate.getTime()) {
                                sessionID = claims.getSubject();
                                if (StringUtils.isNoneBlank(sessionID)) {
                                    RedisUserInfo redisUserInfo = null;
                                    try {
                                        redisUserInfo = RedisUserSession.getUserInfo(sessionID);
                                        if (redisUserInfo != null) {
//                                            if (redisUserInfo.getUserId().toString().equals("6237489035631771648") || redisUserInfo.getUserId().toString().equals("6238019682742484992")) {
//                                                requestContext.getHeaders().add("sessionID", sessionID);
//                                                requestContext.setProperty("userSessionId", sessionID);
//                                            }
                                            requestContext.getHeaders().add("sessionID", sessionID);
                                            requestContext.setProperty("userSessionId", sessionID);
                                        } else {
                                            requestContext.abortWith(responseBuild(408));
                                        }
                                    } catch (RedisSessionException rse) {
                                        requestContext.abortWith(responseBuild(408));
                                    }
                                }
                            } else {
                                requestContext.abortWith(responseBuild(400));
                            }

                        }
                    } catch (Exception e) {
                        requestContext.abortWith(responseBuild(400));
                    }
                } else {
                    if (!requestContext.getMethod().toUpperCase().equals("OPTIONS")) {
                        requestContext.abortWith(responseBuild(400));
                    }
                }
            } else {
                ApiTotalUtils.call(path, "GUEST", System.currentTimeMillis());
            }
        }
    }
}
