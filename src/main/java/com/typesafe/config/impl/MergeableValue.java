package com.typesafe.config.impl;

import com.typesafe.config.ConfigMergeable;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public interface MergeableValue extends ConfigMergeable {
    // 将一个Config转化为一个ConfigValue
    ConfigValue toFallbackValue();
}
