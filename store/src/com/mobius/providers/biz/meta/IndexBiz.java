package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;

public interface IndexBiz {

    String buildDetail(Integer useYear,Integer useMonth) throws BizException;

    String buildIndex() throws BizException;
}
