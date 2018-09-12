package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigMergeable;

import java.io.Serializable;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
final class SimpleConfig implements Config, MergeableValue, Serializable {

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

    @Override
    public SimpleConfig withFallback(ConfigMergeable other) {
        // this can return "this" if the withFallback doesn't need a new
        // ConfigObject
        return object.withFallback(other).toConfig();
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return object;
    }

}
