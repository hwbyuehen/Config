package com.typesafe.config;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public final class ConfigParseOptions {
    final ConfigSyntax syntax;
    final String originDescription;
    final boolean allowMissing;
    final ConfigIncluder includer;
    final ClassLoader classLoader;

    private ConfigParseOptions(ConfigSyntax syntax, String originDescription, boolean allowMissing,
                               ConfigIncluder includer, ClassLoader classLoader) {
        this.syntax = syntax;
        this.originDescription = originDescription;
        this.allowMissing = allowMissing;
        this.includer = includer;
        this.classLoader = classLoader;
    }

    /**
     * 创建ConfigParseOptions实例
     * @return
     */
    public static ConfigParseOptions defaults() {
        return new ConfigParseOptions(null, null, true, null, null);
    }

    /**
     * 设置ClassLoader，new新的实例，不可变的对象
     * @param loader
     * @return
     */
    public ConfigParseOptions setClassLoader(ClassLoader loader) {
        if (this.classLoader == loader)
            return this;
        else
            return new ConfigParseOptions(this.syntax, this.originDescription, this.allowMissing,
                    this.includer, loader);
    }
}
