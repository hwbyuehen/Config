package com.typesafe.config;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public interface Config extends ConfigMergeable {
    
    @Override
    Config withFallback(ConfigMergeable other);

    String getString(String path);
}
