package com.typesafe.config.impl;

import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
abstract class AbstractConfigObject extends AbstractConfigValue implements ConfigObject, Container {

    final private SimpleConfig config;

    protected AbstractConfigObject(ConfigOrigin origin) {
        super(origin);
        this.config = new SimpleConfig(this);
    }

    @Override
    public SimpleConfig toConfig() {
        return config;
    }

    protected abstract AbstractConfigObject newCopy(ResolveStatus status, ConfigOrigin origin);

    @Override
    protected AbstractConfigValue newCopy(ConfigOrigin origin) {
        return newCopy(resolveStatus(), origin);
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.OBJECT;
    }

    @Override
    public AbstractConfigObject withFallback(ConfigMergeable mergeable) {
        return (AbstractConfigObject) super.withFallback(mergeable);
    }
    
}
