package com.typesafe.config;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public interface ConfigMergeable {
    /**
     * 合并两个
     * @param other
     * @return
     */
    ConfigMergeable withFallback(ConfigMergeable other);
}
