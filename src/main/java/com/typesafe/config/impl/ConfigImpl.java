package com.typesafe.config.impl;

import com.typesafe.config.*;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.*;
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
            //TODO
            return null;
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

    private static class DefaultIncluderHolder {
        static final ConfigIncluder defaultIncluder = new SimpleIncluder(null);
    }
    
    //获取单例SimpleIncluder实例
    static ConfigIncluder defaultIncluder() {
        try {
            return DefaultIncluderHolder.defaultIncluder;
        } catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    /**
     * 获取系统环境变量
     * @return
     */
    private static Properties getSystemProperties() {
        // new Properties来put 避免ConcurrentModificationException
        final Properties systemProperties = System.getProperties();
        final Properties systemPropertiesCopy = new Properties();
        synchronized (systemProperties) {
            systemPropertiesCopy.putAll(systemProperties);
        }
        return systemPropertiesCopy;
    }

    private static AbstractConfigObject loadSystemProperties() {
        return (AbstractConfigObject) Parseable.newProperties(getSystemProperties(),
                ConfigParseOptions.defaults().setOriginDescription("system properties")).parse();
    }

    private static class SystemPropertiesHolder {
        // 这个不是最终的，之后有reloadSystemPropertiesConfig的hack
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

    final private static ConfigOrigin defaultValueOrigin = SimpleConfigOrigin
            .newSimple("hardcoded value");
    final private static ConfigBoolean defaultTrueValue = new ConfigBoolean(
            defaultValueOrigin, true);
    final private static ConfigBoolean defaultFalseValue = new ConfigBoolean(
            defaultValueOrigin, false);
    final private static ConfigNull defaultNullValue = new ConfigNull(
            defaultValueOrigin);

    private static ConfigOrigin valueOrigin(String originDescription) {
        if (originDescription == null)
            return defaultValueOrigin;
        else
            return SimpleConfigOrigin.newSimple(originDescription);
    }
    
    public static ConfigValue fromAnyRef(Object object, String originDescription) {
        ConfigOrigin origin = valueOrigin(originDescription);
        return fromAnyRef(object, origin, FromMapMode.KEYS_ARE_KEYS);
    }

    static AbstractConfigValue fromAnyRef(Object object, ConfigOrigin origin,
                                          FromMapMode mapMode) {
        if (origin == null)
            throw new ConfigException.BugOrBroken(
                    "origin not supposed to be null");

        if (object == null) {
            if (origin != defaultValueOrigin)
                return new ConfigNull(origin);
            else
                return defaultNullValue;
        } else if(object instanceof AbstractConfigValue) {
            return (AbstractConfigValue) object;
        } else if (object instanceof Boolean) {
            if (origin != defaultValueOrigin) {
                return new ConfigBoolean(origin, (Boolean) object);
            } else if ((Boolean) object) {
                return defaultTrueValue;
            } else {
                return defaultFalseValue;
            }
        } else if (object instanceof String) {
            return new ConfigString.Quoted(origin, (String) object);
        }/*TODO else if (object instanceof Number) {
            // here we always keep the same type that was passed to us,
            // rather than figuring out if a Long would fit in an Int
            // or a Double has no fractional part. i.e. deliberately
            // not using ConfigNumber.newNumber() when we have a
            // Double, Integer, or Long.
            if (object instanceof Double) {
                return new ConfigDouble(origin, (Double) object, null);
            } else if (object instanceof Integer) {
                return new ConfigInt(origin, (Integer) object, null);
            } else if (object instanceof Long) {
                return new ConfigLong(origin, (Long) object, null);
            } else {
                return ConfigNumber.newNumber(origin,
                        ((Number) object).doubleValue(), null);
            }
        } else if (object instanceof Duration) {
            return new ConfigLong(origin, ((Duration) object).toMillis(), null);
        } else if (object instanceof Map) {
            if (((Map<?, ?>) object).isEmpty())
                return emptyObject(origin);

            if (mapMode == FromMapMode.KEYS_ARE_KEYS) {
                Map<String, AbstractConfigValue> values = new HashMap<String, AbstractConfigValue>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                    Object key = entry.getKey();
                    if (!(key instanceof String))
                        throw new ConfigException.BugOrBroken(
                                "bug in method caller: not valid to create ConfigObject from map with non-String key: "
                                        + key);
                    AbstractConfigValue value = fromAnyRef(entry.getValue(),
                            origin, mapMode);
                    values.put((String) key, value);
                }

                return new SimpleConfigObject(origin, values);
            } else {
                return PropertiesParser.fromPathMap(origin, (Map<?, ?>) object);
            }
        } else if (object instanceof Iterable) {
            Iterator<?> i = ((Iterable<?>) object).iterator();
            if (!i.hasNext())
                return emptyList(origin);

            List<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>();
            while (i.hasNext()) {
                AbstractConfigValue v = fromAnyRef(i.next(), origin, mapMode);
                values.add(v);
            }

            return new SimpleConfigList(origin, values);
        } else if (object instanceof ConfigMemorySize) {
            return new ConfigLong(origin, ((ConfigMemorySize) object).toBytes(), null);
        }*/ else {
            throw new ConfigException.BugOrBroken(
                    "bug in method caller: not valid to create ConfigValue from: "
                            + object);
        }
    }
    

    private static class DebugHolder {
        private static String LOADS = "loads";
        private static String SUBSTITUTIONS = "substitutions";

        private static Map<String, Boolean> loadDiagnostics() {
            Map<String, Boolean> result = new HashMap<String, Boolean>();
            result.put(LOADS, false);
            result.put(SUBSTITUTIONS, false);

            // 用-Dconfig.trace=foo,bar 来开启trace
            String s = System.getProperty("config.trace");
            if (s == null) {
                return result;
            } else {
                String[] keys = s.split(",");
                for (String k : keys) {
                    if (k.equals(LOADS)) {
                        result.put(LOADS, true);
                    } else if (k.equals(SUBSTITUTIONS)) {
                        result.put(SUBSTITUTIONS, true);
                    } else {
                        System.err.println("config.trace property contains unknown trace topic '"
                                + k + "'");
                    }
                }
                return result;
            }
        }

        private static final Map<String, Boolean> diagnostics = loadDiagnostics();

        private static final boolean traceLoadsEnabled = diagnostics.get(LOADS);
        private static final boolean traceSubstitutionsEnabled = diagnostics.get(SUBSTITUTIONS);

        static boolean traceLoadsEnabled() {
            return traceLoadsEnabled;
        }

        static boolean traceSubstitutionsEnabled() {
            return traceSubstitutionsEnabled;
        }
    }

    public static boolean traceLoadsEnabled() {
        try {
            return DebugHolder.traceLoadsEnabled();
        } catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static boolean traceSubstitutionsEnabled() {
        try {
            return DebugHolder.traceSubstitutionsEnabled();
        } catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static void trace(String message) {
        System.err.println(message);
    }

    public static void trace(int indentLevel, String message) {
        while (indentLevel > 0) {
            System.err.print("  ");
            indentLevel -= 1;
        }
        System.err.println(message);
    }
}
