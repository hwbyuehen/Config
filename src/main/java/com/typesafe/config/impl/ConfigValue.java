package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public interface ConfigValue {
    /**
     * 值的源信息
     * @return
     */
    ConfigOrigin origin();

    /**
     * 值的类型
     * @return
     */
    ConfigValueType valueType();
}
