package com.typesafe.config.impl;

import com.typesafe.config.Config;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public class ConfigImpl {
    private static class LoaderCache {
        //当前环境变量参数
        private Config currentSystemProperties;
        //弱引用当前类加载器
        private WeakReference<ClassLoader> currentLoader;
        //缓存Config
        private Map<String, Config> cache;

        LoaderCache() {
            this.currentSystemProperties = null;
            this.currentLoader = new WeakReference<ClassLoader>(null);
            this.cache = new HashMap<String, Config>();
        }
        
        synchronized Config getOrElseUpdate(ClassLoader loader, String key, Callable<Config> updater) {
            //1类加载器变了，则清空缓存
            if (loader != currentLoader.get()) {
                // reset the cache if we start using a different loader
                cache.clear();
                currentLoader = new WeakReference<ClassLoader>(loader);
            }

            //2当前环境变量变了，清空缓存，设置环境变量config
            Config systemProperties = systemPropertiesAsConfig();
            if (systemProperties != currentSystemProperties) {
                cache.clear();
                currentSystemProperties = systemProperties;
            }
        }
    }

    private static class LoaderCacheHolder {
        static final LoaderCache cache = new LoaderCache();
    }

    public static Config computeCachedConfig(ClassLoader loader, String key,
                                             Callable<Config> updater) {
        LoaderCache cache;
        try {
            //静态内部类实现单例模式
            cache = LoaderCacheHolder.cache;
        } catch (ExceptionInInitializerError e) {
            //静态变量初始化时出现异常
            throw ConfigImplUtil.extractInitializerError(e);
        }
        return cache.getOrElseUpdate(loader, key, updater);
    }

    private static class SystemPropertiesHolder {
        // this isn't final due to the reloadSystemPropertiesConfig() hack below
        static volatile AbstractConfigObject systemProperties = loadSystemProperties();
    }

    static AbstractConfigObject systemPropertiesAsConfigObject() {
        try {
            return SystemPropertiesHolder.systemProperties;
        } catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static Config systemPropertiesAsConfig() {
        return systemPropertiesAsConfigObject().toConfig();
    }
}
