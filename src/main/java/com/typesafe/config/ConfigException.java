package com.typesafe.config;

import java.io.Serializable;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public abstract class ConfigException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    final private transient ConfigOrigin origin;

    protected ConfigException(String message, Throwable cause) {
        super(message, cause);
        this.origin = null;
    }

    protected ConfigException(ConfigOrigin origin, String message,
                              Throwable cause) {
        super(origin.description() + ": " + message, cause);
        this.origin = origin;
    }

    /**
     * 值的类型不匹配异常
     */
    public static class WrongType extends ConfigException {
        private static final long serialVersionUID = 1L;

        public WrongType(ConfigOrigin origin, String path, String expected, String actual,
                         Throwable cause) {
            super(origin, path + " has type " + actual + " rather than " + expected, cause);
        }

        public WrongType(ConfigOrigin origin, String path, String expected, String actual) {
            this(origin, path, expected, actual, null);
        }

        public WrongType(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }

        public WrongType(ConfigOrigin origin, String message) {
            super(origin, message, null);
        }
    }

    /**
     * IO相关异常
     */
    public static class IO extends ConfigException {
        private static final long serialVersionUID = 1L;

        public IO(ConfigOrigin origin, String message, Throwable cause) {
            super(origin, message, cause);
        }

        public IO(ConfigOrigin origin, String message) {
            this(origin, message, null);
        }
    }

    /**
     * 处理不了的问题
     */
    public static class BugOrBroken extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BugOrBroken(String message, Throwable cause) {
            super(message, cause);
        }

        public BugOrBroken(String message) {
            this(message, null);
        }
    }

    /**
     * 不属于其他类别的异常
     */
    public static class Generic extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Generic(String message, Throwable cause) {
            super(message, cause);
        }

        public Generic(String message) {
            this(message, null);
        }
    }
}
