package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
final class SimpleConfigObject extends AbstractConfigObject implements Serializable {

    private static final long serialVersionUID = 2L;

    // this map should never be modified - assume immutable
    final private Map<String, AbstractConfigValue> value;
    final private boolean resolved;
    final private boolean ignoresFallbacks;

    SimpleConfigObject(ConfigOrigin origin,
                       Map<String, AbstractConfigValue> value, ResolveStatus status,
                       boolean ignoresFallbacks) {
        super(origin);
        if (value == null)
            throw new ConfigException.BugOrBroken(
                    "creating config object with null map");
        this.value = value;
        this.resolved = status == ResolveStatus.RESOLVED;
        this.ignoresFallbacks = ignoresFallbacks;

        // 昂贵的状态检查
        if (status != ResolveStatus.fromValues(value.values()))
            throw new ConfigException.BugOrBroken("Wrong resolved status on " + this);
    }

    SimpleConfigObject(ConfigOrigin origin,
                       Map<String, AbstractConfigValue> value) {
        this(origin, value, ResolveStatus.fromValues(value.values()), false /* ignoresFallbacks */);
    }
    
    final static SimpleConfigObject emptyMissing(ConfigOrigin baseOrigin) {
        return new SimpleConfigObject(SimpleConfigOrigin.newSimple(
                baseOrigin.description() + " (not found)"),
                Collections.<String, AbstractConfigValue> emptyMap());
    }

    private SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin,
                                       boolean newIgnoresFallbacks) {
        return new SimpleConfigObject(newOrigin, value, newStatus, newIgnoresFallbacks);
    }

    @Override
    protected SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin) {
        return newCopy(newStatus, newOrigin, ignoresFallbacks);
    }
}
