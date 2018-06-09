package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.common.BizException;
import com.mobius.common.StoreException;
import com.mobius.providers.biz.meta.IndexBiz;
import com.mobius.providers.biz.meta.WeightBiz;
import net.sf.json.JSONObject;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class WeightBizImp extends BaseBiz implements WeightBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;


    @Override
    public String cal(Integer useYear, Integer useMonth, Integer diffDay) throws BizException {
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
    public String info(Integer useYear, Integer useMonth) throws BizException {
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
