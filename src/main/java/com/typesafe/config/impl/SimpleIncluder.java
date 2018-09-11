package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigIncluder;
import com.typesafe.config.ConfigParseOptions;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
class SimpleIncluder implements FullIncluder {
    private ConfigIncluder fallback;
    
    public SimpleIncluder(ConfigIncluder fallback) {
        this.fallback = fallback;
    }

    //清除Syntax，源描述，allowMissing
    static ConfigParseOptions clearForInclude(ConfigParseOptions options) {
        return options.setSyntax(null).setOriginDescription(null).setAllowMissing(true);
    }

    @Override
    public ConfigIncluder withFallback(ConfigIncluder fallback) {
        if (this == fallback) {
            throw new ConfigException.BugOrBroken("trying to create includer cycle");
        } else if (this.fallback == fallback) {
            return this;
        } else if (this.fallback != null) {
            return new SimpleIncluder(this.fallback.withFallback(fallback));
        } else {
            return new SimpleIncluder(fallback);
        }
    }

    static private class Proxy implements FullIncluder {
        final ConfigIncluder delegate;

        Proxy(ConfigIncluder delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigIncluder withFallback(ConfigIncluder fallback) {
            //不合并
            return this;
        }
    }

    /**
     * 若类型不是FullIncluder，则使用代理类
     * @param includer
     * @return
     */
    static FullIncluder makeFull(ConfigIncluder includer) {
        if (includer instanceof FullIncluder)
            return (FullIncluder) includer;
        else
            return new Proxy(includer);
    }
}
