package com.mobius.common;


/**
 * Created by zhenjiawang on 2016/12/30.
 */
public class StoreException extends RuntimeException {

    public StoreException() {
        super();
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }

    protected StoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
