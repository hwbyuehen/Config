package com.typesafe.config;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public interface ConfigOrigin {

    /**
     * 返回一个值或异常的描述源信息，不会返回null
     * @return
     */
    public String description();
}
