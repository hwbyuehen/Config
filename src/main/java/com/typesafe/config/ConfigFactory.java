package com.typesafe.config;

import com.typesafe.config.impl.ConfigImpl;

import java.util.concurrent.Callable;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public final class ConfigFactory {
    
    private static ClassLoader checkedContextClassLoader(String methodName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            throw new ConfigException.BugOrBroken("Context class loader is not set for the current thread; "
                    + "if Thread.currentThread().getContextClassLoader() returns null, you must pass a ClassLoader "
                    + "explicitly to ConfigFactory." + methodName);
        else
            return loader;
    }
    
    /**
     * 加载Config配置项
     * @return
     */
    public static Config load() {
        ClassLoader loader = checkedContextClassLoader("load");
        return load(loader);
    }

    public static Config load(final ClassLoader loader) {
        //1创建自定义配置选项
        final ConfigParseOptions withLoader = ConfigParseOptions.defaults().setClassLoader(loader);
        //2处理cache的config
        return ConfigImpl.computeCachedConfig(loader, "load", new Callable<Config>() {
            @Override
            public Config call() {
                return load(loader, defaultApplication(withLoader));
            }
        });
    }
}
