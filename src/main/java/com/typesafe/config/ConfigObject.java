package com.typesafe.config;

import com.typesafe.config.impl.ConfigValue;

import java.util.Map;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public interface ConfigObject extends ConfigValue, Map<String, ConfigValue> {

    /**
     * 转换这个对象为Config实例，从而可以通过path找到值
     * @return
     */
    Config toConfig();
}
