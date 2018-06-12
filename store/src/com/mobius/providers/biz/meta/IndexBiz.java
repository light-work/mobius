package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;

public interface IndexBiz {

    String buildIndex() throws BizException;

    String index() throws BizException;

    String historyIndex() throws BizException;
}
