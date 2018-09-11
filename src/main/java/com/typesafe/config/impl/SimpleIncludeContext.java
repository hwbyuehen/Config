package com.typesafe.config.impl;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigParseOptions;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public class SimpleIncludeContext implements ConfigIncludeContext {
    private final Parseable parseable;
    private final ConfigParseOptions options;
    
    public SimpleIncludeContext(Parseable parseable) {
        this.parseable = parseable;
        this.options = SimpleIncluder.clearForInclude(parseable.options());
    }

}
