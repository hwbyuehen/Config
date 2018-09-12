package com.typesafe.config.impl;

import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
abstract class AbstractConfigValue implements ConfigValue, MergeableValue {
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

    @Override
    public AbstractConfigValue withFallback(ConfigMergeable other) {
//        if (ignoresFallbacks()) {TODO
//            return this;
//        } else {
//            ConfigValue other = ((MergeableValue) mergeable).toFallbackValue();
//
//            if (other instanceof Unmergeable) {
//                return mergedWithTheUnmergeable((Unmergeable) other);
//            } else if (other instanceof AbstractConfigObject) {
//                return mergedWithObject((AbstractConfigObject) other);
//            } else {
//                return mergedWithNonObject((AbstractConfigValue) other);
//            }
//        }
        return null;
    }

    @Override
    public AbstractConfigValue toFallbackValue() {
        return this;
    }

}
