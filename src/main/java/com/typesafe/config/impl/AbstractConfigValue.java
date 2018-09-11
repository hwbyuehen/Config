package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
abstract class AbstractConfigValue implements ConfigValue {
    final private SimpleConfigOrigin origin;

    AbstractConfigValue(ConfigOrigin origin) {
        this.origin = (SimpleConfigOrigin) origin;
    }

    ResolveStatus resolveStatus() {
        return ResolveStatus.RESOLVED;
    }

    @Override
    public SimpleConfigOrigin origin() {
        return this.origin;
    }

    protected abstract AbstractConfigValue newCopy(ConfigOrigin origin);


}
