package com.typesafe.config.impl;

import com.typesafe.config.Config;

import java.io.Serializable;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
final class SimpleConfig implements Config, Serializable {

    private static final long serialVersionUID = 1L;

    final private AbstractConfigObject object;

    SimpleConfig(AbstractConfigObject object) {
        this.object = object;
    }
    
    @Override
    public String getString(String path) {
//        ConfigValue v = find(path, ConfigValueType.STRING);
//        return (String) v.unwrapped();
        return null;
    }
}
