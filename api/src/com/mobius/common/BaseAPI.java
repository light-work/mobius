package com.mobius.common;

import com.google.inject.Inject;
import net.sf.json.JSONObject;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.hsf.HSFServiceFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by gbcp on 2016/12/31.
 */
public class BaseAPI {
    @Inject
    protected HSFServiceFactory hsfServiceFactory;

    protected JSONObject buildResult(JSONObject apiResultObj, StringBuilder errorBuilder, String bizResult) {
        int r = -1;
        if (errorBuilder == null) {
            errorBuilder = new StringBuilder();
        }
        if (StringUtils.isNotBlank(bizResult)) {
            JSONObject resultObj = JSONObject.fromObject(bizResult);
            if (resultObj != null && resultObj.containsKey("result")) {
                r = resultObj.getInt("result");
                apiResultObj.putAll(resultObj);
                if (r == 0) {
                    errorBuilder = new StringBuilder();
                    errorBuilder.append("ok");
                    apiResultObj.remove("result");
                } else {
                    if (resultObj.containsKey("errorMsg")) {
                        errorBuilder.append(resultObj.getString("errorMsg"));
                    }
                }
            }
        }
        apiResultObj.put("errorMsg", errorBuilder.toString());
        apiResultObj.put("errorCode", r);
        return apiResultObj;
    }

    protected JSONObject buildResult(JSONObject apiResultObj, StringBuilder errorBuilder, boolean success) {
        int r = -1;
        if (errorBuilder == null) {
            errorBuilder = new StringBuilder();
        }
        if (success) {
            r = 0;
            errorBuilder = new StringBuilder();
            errorBuilder.append("ok");
        }
        apiResultObj.put("errorMsg", errorBuilder.toString());
        apiResultObj.put("errorCode", r);
        return apiResultObj;
    }


    public String download(HttpServletResponse httpServletResponse, InputStream in, String fileName, long fileLength) throws Exception {
        try {
// 取得文件名。
// 取得文件的后缀名。
// 以流的形式下载文件。
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
// 清空response
            httpServletResponse.reset();
// 设置response的Header
            httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
            httpServletResponse.addHeader("Content-Length", "" + fileLength);
            OutputStream toClient = new BufferedOutputStream(httpServletResponse.getOutputStream());
            httpServletResponse.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {

        }
        return null;
    }
}
