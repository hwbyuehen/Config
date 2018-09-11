package com.typesafe.config.impl;

import java.util.Collection;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
enum ResolveStatus {
    UNRESOLVED, RESOLVED;

    final static ResolveStatus fromValues(
            Collection<? extends AbstractConfigValue> values) {
        for (AbstractConfigValue v : values) {
            if (v.resolveStatus() == ResolveStatus.UNRESOLVED)
                return ResolveStatus.UNRESOLVED;
        }
        return ResolveStatus.RESOLVED;
    }

    final static ResolveStatus fromBoolean(boolean resolved) {
        return resolved ? ResolveStatus.RESOLVED : ResolveStatus.UNRESOLVED;
    }
}
