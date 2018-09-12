package com.typesafe.config;

import com.typesafe.config.impl.ConfigValue;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/12
 */
public class ConfigResolveOptions {
    private final boolean useSystemEnvironment;
    private final boolean allowUnresolved;
    private final ConfigResolver resolver;

    private ConfigResolveOptions(boolean useSystemEnvironment, boolean allowUnresolved,
                                 ConfigResolver resolver) {
        this.useSystemEnvironment = useSystemEnvironment;
        this.allowUnresolved = allowUnresolved;
        this.resolver = resolver;
    }

    /**
     * Returns the default resolve options. By default the system environment
     * will be used and unresolved substitutions are not allowed.
     *
     * @return the default resolve options
     */
    public static ConfigResolveOptions defaults() {
        return new ConfigResolveOptions(true, false, NULL_RESOLVER);
    }

    /**
     * Singleton resolver that never resolves paths.
     */
    private static final ConfigResolver NULL_RESOLVER = new ConfigResolver() {

        @Override
        public ConfigValue lookup(String path) {
            return null;
        }

        @Override
        public ConfigResolver withFallback(ConfigResolver fallback) {
            return fallback;
        }

    };
}
