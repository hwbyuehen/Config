package com.typesafe.config;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public interface ConfigIncluder {

    /**
     * 合并两个ConfigIncluder
     * @param fallback
     * @return
     */
    ConfigIncluder withFallback(ConfigIncluder fallback);
}
