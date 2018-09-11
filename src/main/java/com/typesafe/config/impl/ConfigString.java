package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;

import java.io.ObjectStreamException;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
abstract class ConfigString extends AbstractConfigValue {
    final protected String value;
    
    ConfigString(ConfigOrigin origin, String value) {
        super(origin);
        this.value = value;
    }

    final static class Quoted extends ConfigString {
        Quoted(ConfigOrigin origin, String value) {
            super(origin, value);
        }
        @Override
        protected Quoted newCopy(ConfigOrigin origin) {
            return new Quoted(origin, value);
        }
        // serialization all goes through SerializedConfigValue
        private Object writeReplace() throws ObjectStreamException {
            return new SerializedConfigValue(this);
        }
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.STRING;
    }

}
