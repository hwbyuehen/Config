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
}
