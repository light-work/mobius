package com.mobius.action;

import org.guiceside.commons.lang.StringUtils;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

@Action(name = "test", namespace = "/mobius")
public class TestAction extends BaseAction {

    @ReqGet
    private Long tradeId;

    @ReqGet
    private String market;

    @Override
    public String execute() throws Exception {
        System.out.println("qwqwqw"+market);
        return null;
    }


    public String hash() throws Exception {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}