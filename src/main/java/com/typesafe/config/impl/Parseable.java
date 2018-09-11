package com.typesafe.config.impl;

import com.typesafe.config.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
public abstract class Parseable implements ConfigParseable {

    private ConfigIncludeContext includeContext;
    private ConfigParseOptions initialOptions;
    private ConfigOrigin initialOrigin;

    protected Parseable() {
    }

    private ConfigParseOptions fixupOptions(ConfigParseOptions baseOptions) {
        //1设置ConfigParseOptions默认Syntax为CONF
        ConfigSyntax syntax = baseOptions.getSyntax();
        if (syntax == null) {
            syntax = guessSyntax();
        }
        if (syntax == null) {
            syntax = ConfigSyntax.CONF;
        }
        ConfigParseOptions modified = baseOptions.setSyntax(syntax);

        //2设置ConfigParseOptions默认ConfigIncluder
        modified = modified.appendIncluder(ConfigImpl.defaultIncluder());
        modified = modified.setIncluder(SimpleIncluder.makeFull(modified.getIncluder()));

        return modified;
    }

    protected void postConstruct(ConfigParseOptions baseOptions) {
        //设置默认Syntax，和ConfigIncluder
        this.initialOptions = fixupOptions(baseOptions);

        this.includeContext = new SimpleIncludeContext(this);

        if (initialOptions.getOriginDescription() != null)
            initialOrigin = SimpleConfigOrigin.newSimple(initialOptions.getOriginDescription());
        else
            initialOrigin = createOrigin();
    }

    public ConfigObject parse() {
        return forceParsedToObject(parseValue(options()));
    }

    final AbstractConfigValue parseValue(ConfigParseOptions baseOptions) {
        // 没用initialOptions
        ConfigParseOptions options = fixupOptions(baseOptions);

        // passed-in options can override origin
        ConfigOrigin origin;
        if (options.getOriginDescription() != null)
            origin = SimpleConfigOrigin.newSimple(options.getOriginDescription());
        else
            origin = initialOrigin;
        return parseValue(origin, options);
    }

    final private AbstractConfigValue parseValue(ConfigOrigin origin,
                                                 ConfigParseOptions finalOptions) {
        try {
            return rawParseValue(origin, finalOptions);
        } catch (IOException e) {
            if (finalOptions.getAllowMissing()) {
                trace(e.getMessage() + ". Allowing Missing File, this can be turned off by setting" +
                        " ConfigParseOptions.allowMissing = false");
                return SimpleConfigObject.emptyMissing(origin);
            } else {
                trace("exception loading " + origin.description() + ": " + e.getClass().getName()
                        + ": " + e.getMessage());
                throw new ConfigException.IO(origin,
                        e.getClass().getName() + ": " + e.getMessage(), e);
            }
        }
    }

    static AbstractConfigObject forceParsedToObject(ConfigValue value) {
        if (value instanceof AbstractConfigObject) {
            return (AbstractConfigObject) value;
        } else {
            throw new ConfigException.WrongType(value.origin(), "", "object at file root", value
                    .valueType().name());
        }
    }

    // this is parseValue without post-processing the IOException or handling
    // options.getAllowMissing()
    protected AbstractConfigValue rawParseValue(ConfigOrigin origin, ConfigParseOptions finalOptions)
            throws IOException {
        Reader reader = reader(finalOptions);

        // after reader() we will have loaded the Content-Type.
        ConfigSyntax contentType = contentType();

        ConfigParseOptions optionsWithContentType;
        if (contentType != null) {
            if (ConfigImpl.traceLoadsEnabled() && finalOptions.getSyntax() != null)
                trace("Overriding syntax " + finalOptions.getSyntax()
                        + " with Content-Type which specified " + contentType);

            optionsWithContentType = finalOptions.setSyntax(contentType);
        } else {
            optionsWithContentType = finalOptions;
        }

        try {
            return rawParseValue(reader, origin, optionsWithContentType);
        } finally {
            reader.close();
        }
    }

    private AbstractConfigValue rawParseValue(Reader reader, ConfigOrigin origin,
                                              ConfigParseOptions finalOptions) throws IOException {
        if (finalOptions.getSyntax() == ConfigSyntax.PROPERTIES) {
            return PropertiesParser.parse(reader, origin);
        } else {
            //TODO
            return null;
//            Iterator<Token> tokens = Tokenizer.tokenize(origin, reader, finalOptions.getSyntax());
//            ConfigNodeRoot document = ConfigDocumentParser.parse(tokens, origin, finalOptions);
//            return ConfigParser.parse(document, origin, finalOptions, includeContext());
        }
    }

    protected abstract Reader reader() throws IOException;

    protected Reader reader(ConfigParseOptions options) throws IOException {
        return reader();
    }

    ConfigIncludeContext includeContext() {
        return includeContext;
    }

    protected static void trace(String message) {
        if (ConfigImpl.traceLoadsEnabled()) {
            ConfigImpl.trace(message);
        }
    }

    ConfigSyntax guessSyntax() {
        return null;
    }

    ConfigSyntax contentType() {
        return null;
    }

    protected abstract ConfigOrigin createOrigin();
    
    @Override
    public ConfigParseOptions options() {
        return initialOptions;
    }
    

    private final static class ParseableProperties extends Parseable {
        final private Properties props;

        ParseableProperties(Properties props, ConfigParseOptions options) {
            this.props = props;
            postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            throw new ConfigException.BugOrBroken("reader() should not be called on props");
        }

        @Override
        protected AbstractConfigObject rawParseValue(ConfigOrigin origin,
                                                     ConfigParseOptions finalOptions) {
            if (ConfigImpl.traceLoadsEnabled())
                trace("Loading config from properties " + props);
            return PropertiesParser.fromProperties(origin, props);
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigSyntax.PROPERTIES;
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("properties");
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + props.size() + " props)";
        }
    }

    public static Parseable newProperties(Properties properties, ConfigParseOptions options) {
        return new ParseableProperties(properties, options);
    }
}
