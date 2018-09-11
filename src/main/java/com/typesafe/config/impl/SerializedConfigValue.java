package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public class SerializedConfigValue extends AbstractConfigValue {

    private ConfigValue value;
    private boolean wasConfig;

    // 用java的反序列化
    public SerializedConfigValue() {
        super(null);
    }
    
    SerializedConfigValue(ConfigValue value) {
        this();
        this.value = value;
        this.wasConfig = false;
    }

    @Override
    protected SerializedConfigValue newCopy(ConfigOrigin origin) {
        throw shouldNotBeUsed();
    }

    private static ConfigException shouldNotBeUsed() {
        return new ConfigException.BugOrBroken(SerializedConfigValue.class.getName()
                + " should not exist outside of serialization");
    }

    @Override
    public ConfigValueType valueType() {
        throw shouldNotBeUsed();
    }
}
