package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;

public interface DailyBiz {

    String buildDaily(String tradeSign,String market) throws BizException;

}
