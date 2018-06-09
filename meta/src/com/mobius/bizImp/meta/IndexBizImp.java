package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.common.BizException;
import com.mobius.common.StoreException;
import com.mobius.providers.biz.meta.DailyBiz;
import com.mobius.providers.biz.meta.IndexBiz;
import net.sf.json.JSONObject;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class IndexBizImp extends BaseBiz implements IndexBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String buildDetail(Integer useYear,Integer useMonth) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String buildIndex() throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }


}
