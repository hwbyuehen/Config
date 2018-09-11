package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;

/**
 * Created by 吃土的飞鱼 on 2018/9/11.
 */
public class ConfigImplUtil {
    /**
     * 判断对象是否相当，包含null判断
     * @param a
     * @param b
     * @return
     */
    static boolean equalsHandlingNull(Object a, Object b) {
        if (a == null && b != null)
            return false;
        else if (a != null && b == null)
            return false;
        else if (a == b) // catches null == null plus optimizes identity case
            return true;
        else
            return a.equals(b);
    }
    
    static boolean isC0Control(int codepoint) {
        return (codepoint >= 0x0000 && codepoint <= 0x001F);
    }

    /**
     * 转换为json字符
     * @param s
     * @return
     */
    public static String renderJsonString(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (isC0Control(c))
                        sb.append(String.format("\\u%04x", (int) c));
                    else
                        sb.append(c);
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * 如果静态变量初始化发生异常，则封装ConfigException异常
     * @param e
     * @return
     */
    public static ConfigException extractInitializerError(ExceptionInInitializerError e) {
        Throwable cause = e.getCause();
        if (cause != null && cause instanceof ConfigException) {
            return (ConfigException) cause;
        } else {
            throw e;
        }
    }
    
}
