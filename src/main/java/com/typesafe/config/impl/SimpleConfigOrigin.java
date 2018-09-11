package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;

import java.util.List;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public class SimpleConfigOrigin implements ConfigOrigin {

    final private String description;
    final private int lineNumber;
    final private int endLineNumber;
    final private OriginType originType;
    final private String urlOrNull;
    final private String resourceOrNull;
    final private List<String> commentsOrNull;

    protected SimpleConfigOrigin(String description, int lineNumber, int endLineNumber, OriginType originType,
                                 String urlOrNull, String resourceOrNull, List<String> commentsOrNull) {
        if (description == null)
            throw new ConfigException.BugOrBroken("description may not be null");
        this.description = description;
        this.lineNumber = lineNumber;
        this.endLineNumber = endLineNumber;
        this.originType = originType;
        this.urlOrNull = urlOrNull;
        this.resourceOrNull = resourceOrNull;
        this.commentsOrNull = commentsOrNull;
    }

    /**
     * 静态方法创建源信息
     * @param description
     * @return
     */
    static SimpleConfigOrigin newSimple(String description) {
        return new SimpleConfigOrigin(description, -1, -1, OriginType.GENERIC, null, null, null);
    }

    @Override
    public String description() {
        if (lineNumber < 0) {
            return description;
        } else if (endLineNumber == lineNumber) {
            return description + ": " + lineNumber;
        } else {
            return description + ": " + lineNumber + "-" + endLineNumber;
        }
    }
}
