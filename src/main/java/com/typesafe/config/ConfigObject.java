package com.typesafe.config;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public interface ConfigObject {

    /**
     * 转换这个对象为Config实例，从而可以通过path找到值
     * @return
     */
    Config toConfig();
}
