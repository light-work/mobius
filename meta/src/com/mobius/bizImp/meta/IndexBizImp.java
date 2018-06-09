package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.common.BizException;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.providers.biz.meta.DailyBiz;
import com.mobius.providers.biz.meta.IndexBiz;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import net.sf.json.JSONObject;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.Date;
import java.util.List;

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
            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore=hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            if(calSampleSpotSymbolWeightStore!=null){
                Date useDate=DateFormatUtil.parse(useYear+"-"+useMonth+"-01",DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                Date beforeDate=DateFormatUtil.addMonth(useDate,-1);
                Integer year=DateFormatUtil.getDayInYear(beforeDate);
                Integer month=DateFormatUtil.getDayInMonth(beforeDate)-1;
                List<CalSampleSpotSymbolWeight> sampleSpotSymbolWeightList= calSampleSpotSymbolWeightStore.getListByYearMonth(year,month);
                if(sampleSpotSymbolWeightList!=null&&!sampleSpotSymbolWeightList.isEmpty()){

                }
            }
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
