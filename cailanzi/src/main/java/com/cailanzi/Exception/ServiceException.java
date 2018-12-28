package com.cailanzi.exception;

/**
 * Created by v-hel27 on 2018/8/7.
 */
public class ServiceException extends RuntimeException{

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
