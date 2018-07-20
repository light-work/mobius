package com.mobius.apis;

import com.mobius.common.BaseAPI;
import com.mobius.providers.biz.meta.IndexBiz;
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


    @Path("/index")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response index() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        try {
            IndexBiz indexBiz = hsfServiceFactory.consumer(IndexBiz.class);
            if (indexBiz != null) {
                bizResult = indexBiz.index();
                System.out.println(bizResult);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/historyIndex")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response historyIndex() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        try {
            IndexBiz indexBiz = hsfServiceFactory.consumer(IndexBiz.class);
            if (indexBiz != null) {
                bizResult = indexBiz.historyIndex();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }
}
