package com.typesafe.config;

import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Parseable;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public final class ConfigFactory {
    private static final String STRATEGY_PROPERTY_NAME = "config.strategy";
    
    private static ClassLoader checkedContextClassLoader(String methodName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            throw new ConfigException.BugOrBroken("Context class loader is not set for the current thread; "
                    + "if Thread.currentThread().getContextClassLoader() returns null, you must pass a ClassLoader "
                    + "explicitly to ConfigFactory." + methodName);
        else
            return loader;
    }

    private static ConfigParseOptions ensureClassLoader(ConfigParseOptions options, String methodName) {
        if (options.getClassLoader() == null)
            return options.setClassLoader(checkedContextClassLoader(methodName));
        else
            return options;
    }
    
    /**
     * 加载Config配置项
     * @return
     */
    public static Config load() {
        ClassLoader loader = checkedContextClassLoader("load");
        return load(loader);
    }

    public static Config load(ClassLoader loader, Config config) {
        return load(loader, config, ConfigResolveOptions.defaults());
    }

    public static Config load(ClassLoader loader, Config config, ConfigResolveOptions resolveOptions) {
        return defaultOverrides(loader).withFallback(config).withFallback(defaultReference(loader))
                .resolve(resolveOptions);
    }

    public static Config load(final ClassLoader loader) {
        //1创建自定义配置选项
        final ConfigParseOptions withLoader = ConfigParseOptions.defaults().setClassLoader(loader);
        //2处理cache的config
        return ConfigImpl.computeCachedConfig(loader, "load", new Callable<Config>() {
            @Override
            public Config call() {
                //加载自定义配置文件
                return load(loader, defaultApplication(withLoader));
            }
        });
    }

    public static Config defaultReference(ClassLoader loader) {
        return ConfigImpl.defaultReference(loader);
    }

    /**
     * 获得环境变量的Config
     */
    public static Config defaultOverrides(ClassLoader loader) {
        return systemProperties();
    }

    /**
     * 获取环境变量的Config
     */
    public static Config systemProperties() {
        return ConfigImpl.systemPropertiesAsConfig();
    }

    /**
     * 解析所有resources在classpath上为一个Config
     */
    public static Config parseResources(ClassLoader loader, String resource,
                                        ConfigParseOptions options) {
        return parseResources(resource, options.setClassLoader(loader));
    }

    public static Config parseResources(String resource, ConfigParseOptions options) {
        //获取ClassLoader
        ConfigParseOptions withLoader = ensureClassLoader(options, "parseResources");
        return Parseable.newResources(resource, withLoader).parse().toConfig();
    }

    /**
     * 解析一个file为一个Config实例，不调用{@link Config#resolve}
     */
    public static Config parseFile(File file, ConfigParseOptions options) {
        return Parseable.newFile(file, options).parse().toConfig();
    }

    /**
     * 解析一个URL为一个Config实例，不调用{@link Config#resolve}
     */
    public static Config parseURL(URL url, ConfigParseOptions options) {
        return Parseable.newURL(url, options).parse().toConfig();
    }

    /**
     * {@link #defaultApplication()}相同，但是可以让你指定解析选项.
     */
    public static Config defaultApplication(ConfigParseOptions options) {
        return getConfigLoadingStrategy().parseApplicationConfig(ensureClassLoader(options, "defaultApplication"));
    }

    /**
     * 解析resource任何。。
     */
    public static Config parseResourcesAnySyntax(String resourceBasename, ConfigParseOptions options) {
        return ConfigImpl.parseResourcesAnySyntax(resourceBasename, options).toConfig();
    }

    /**
     * 返回自定义或默认的Config加载策略
     * @return
     */
    private static ConfigLoadingStrategy getConfigLoadingStrategy() {
        String className = System.getProperties().getProperty(STRATEGY_PROPERTY_NAME);

        if (className != null) {
            try {
                return ConfigLoadingStrategy.class.cast(Class.forName(className).newInstance());
            } catch (Throwable e) {
                throw new ConfigException.BugOrBroken("Failed to load strategy: " + className, e);
            }
        } else {
            return new DefaultConfigLoadingStrategy();
        }
    }
}
