package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;

public interface WeightBiz {

    String cal(Integer useYear, Integer useMonth, Integer diffDay) throws BizException;

    String info(Integer useYear, Integer useMonth) throws BizException;
}
