package com.typesafe.config.impl;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public interface Container extends ConfigValue {
    /**
     * 替换Child为replacement
     */
    AbstractConfigValue replaceChild(AbstractConfigValue child, AbstractConfigValue replacement);

    /**
     * 超级昂贵的完整遍历，看看descendant是否在这个容器下面的任何地方。
     */
    boolean hasDescendant(AbstractConfigValue descendant);
}
