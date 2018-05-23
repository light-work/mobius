package com.mobius.utils;


import java.security.Key;

/**
 * Created by gbcp on 2016/12/29.
 */
public final class JwtKey {

    private Key key;

    private static class SingletonHolder {
        private static final JwtKey INSTANCE = new JwtKey();
    }


    private JwtKey() {
    }

    public static final JwtKey getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
