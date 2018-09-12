package com.typesafe.config;

import com.typesafe.config.impl.ConfigValue;

/**
 * ConfigResolveOptions中用来自定义处理resolve
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public interface ConfigResolver {
    /**
     * 处理未resolve的path，返回值
     *
     * @param path the unresolved path
     * @return the value to use as a substitution or null
     */
    public ConfigValue lookup(String path);

    /**
     * 合并两个
     */
    public ConfigResolver withFallback(ConfigResolver fallback);

}
