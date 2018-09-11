package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public class ConfigNull extends AbstractConfigValue {
    ConfigNull(ConfigOrigin origin) {
        super(origin);
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NULL;
    }

    @Override
    protected ConfigNull newCopy(ConfigOrigin origin) {
        return new ConfigNull(origin);
    }

}
