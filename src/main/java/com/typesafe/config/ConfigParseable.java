package com.typesafe.config;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public interface ConfigParseable {

    /**
     * 获取初始化的解析options
     * @return
     */
    ConfigParseOptions options();
}
