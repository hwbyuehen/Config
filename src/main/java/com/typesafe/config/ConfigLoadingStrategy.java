package com.typesafe.config;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public interface ConfigLoadingStrategy {
    /**
     * 加载和解析应用的配置
     */
    Config parseApplicationConfig(ConfigParseOptions parseOptions);
}
