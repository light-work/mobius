package com.mobius.common;

import net.sf.json.JSONObject;

import javax.ws.rs.core.Response;

/**
 * Created by gbcp on 2016/12/31.
 */
public class BaseFilter {
    protected Response responseBuild(int status) {
        JSONObject authObj = new JSONObject();
        authObj.put("errorCode", status);
        return Response.ok().entity(authObj.toString()).build();
    }
}
