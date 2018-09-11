package com.typesafe.config.impl;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public class AbstractConfigObject extends AbstractConfigValue implements ConfigObject {

    final private SimpleConfig config;

    protected AbstractConfigObject(ConfigOrigin origin) {
        super(origin);
        this.config = new SimpleConfig(this);
    }
    
    @Override
    public SimpleConfig toConfig() {
        return config;
    }
}
