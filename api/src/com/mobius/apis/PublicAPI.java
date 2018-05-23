package com.mobius.apis;

import com.mobius.common.BaseAPI;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by gbcp on 16/8/8.
 */
@Path("/public")
public class PublicAPI extends BaseAPI {


    @Context
    private HttpServletRequest httpServletRequest;


    @Context
    private HttpServletResponse httpServletResponse;


    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }


    @Path("/verifyCode")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response verifyCode() {
        JSONObject result = new JSONObject();
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Response.ok().entity(result.toString()).build();
    }

}
