package com.typesafe.config;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public final class ConfigParseOptions {
    final ConfigSyntax syntax;
    final String originDescription;//组装ConfigOrigin
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
     * 设置配置的语法（json，conf，properties）
     * @param syntax
     * @return
     */
    public ConfigParseOptions setSyntax(ConfigSyntax syntax) {
        if (this.syntax == syntax)
            return this;
        else
            return new ConfigParseOptions(syntax, this.originDescription, this.allowMissing,
                    this.includer, this.classLoader);
    }

    public ConfigSyntax getSyntax() {
        return syntax;
    }

    /**
     * 设置源描述信息
     * @param originDescription
     * @return
     */
    public ConfigParseOptions setOriginDescription(String originDescription) {
        // findbugs complains about == here but is wrong, do not "fix"
        if (this.originDescription == originDescription)
            return this;
        else if (this.originDescription != null && originDescription != null
                && this.originDescription.equals(originDescription))
            return this;
        else
            return new ConfigParseOptions(this.syntax, originDescription, this.allowMissing,
                    this.includer, this.classLoader);
    }

    public String getOriginDescription() {
        return originDescription;
    }

    public ConfigParseOptions setAllowMissing(boolean allowMissing) {
        if (this.allowMissing == allowMissing)
            return this;
        else
            return new ConfigParseOptions(this.syntax, this.originDescription, allowMissing,
                    this.includer, this.classLoader);
    }

    public boolean getAllowMissing() {
        return allowMissing;
    }

    public ConfigParseOptions setIncluder(ConfigIncluder includer) {
        if (this.includer == includer)
            return this;
        else
            return new ConfigParseOptions(this.syntax, this.originDescription, this.allowMissing,
                    includer, this.classLoader);
    }

    /**
     * 合并ConfigIncluder并设置
     * @param includer
     * @return
     */
    public ConfigParseOptions appendIncluder(ConfigIncluder includer) {
        if (includer == null)
            throw new NullPointerException("null includer passed to appendIncluder");
        if (this.includer == includer)
            return this;
        else if (this.includer != null)
            return setIncluder(this.includer.withFallback(includer));
        else
            return setIncluder(includer);
    }

    public ConfigIncluder getIncluder() {
        return includer;
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
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
