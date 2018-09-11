package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public class ConfigImplUtil {

    /**
     * 如果静态变量初始化发生异常，则封装ConfigException异常
     * @param e
     * @return
     */
    public static ConfigException extractInitializerError(ExceptionInInitializerError e) {
        Throwable cause = e.getCause();
        if (cause != null && cause instanceof ConfigException) {
            return (ConfigException) cause;
        } else {
            throw e;
        }
    }
    
}
