package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;

import java.util.Iterator;
import java.util.List;

/**
 * @author 吃土的飞鱼
 * @date 2018/9/11
 */
final class Path {

    //单链表，从path字符串的最后-》最前
    final private String first;
    final private Path remainder;

    Path(String first, Path remainder) {
        this.first = first;
        this.remainder = remainder;
    }

    Path(String... elements) {
        if (elements.length == 0)
            throw new ConfigException.BugOrBroken("empty path");
        this.first = elements[0];
        if (elements.length > 1) {
            PathBuilder pb = new PathBuilder();
            for (int i = 1; i < elements.length; ++i) {
                pb.appendKey(elements[i]);
            }
            this.remainder = pb.result();
        } else {
            this.remainder = null;
        }
    }

    Path(List<Path> pathsToConcat) {
        this(pathsToConcat.iterator());
    }

    Path(Iterator<Path> i) {
        if (!i.hasNext())
            throw new ConfigException.BugOrBroken("empty path");

        Path firstPath = i.next();
        this.first = firstPath.first;

        PathBuilder pb = new PathBuilder();
        if (firstPath.remainder != null) {
            pb.appendPath(firstPath.remainder);
        }
        while (i.hasNext()) {
            pb.appendPath(i.next());
        }
        this.remainder = pb.result();
    }

    String first() {
        return first;
    }

    Path remainder() {
        return remainder;
    }

    Path parent() {
        if (remainder == null)
            return null;

        PathBuilder pb = new PathBuilder();
        Path p = this;
        while (p.remainder != null) {
            pb.appendKey(p.first);
            p = p.remainder;
        }
        return pb.result();
    }

    String last() {
        Path p = this;
        while (p.remainder != null) {
            p = p.remainder;
        }
        return p.first;
    }

    Path prepend(Path toPrepend) {
        PathBuilder pb = new PathBuilder();
        pb.appendPath(toPrepend);
        pb.appendPath(this);
        return pb.result();
    }

    int length() {
        int count = 1;
        Path p = remainder;
        while (p != null) {
            count += 1;
            p = p.remainder;
        }
        return count;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Path) {
            Path that = (Path) other;
            return this.first.equals(that.first)
                    && ConfigImplUtil.equalsHandlingNull(this.remainder,
                    that.remainder);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 41 * (41 + first.hashCode())
                + (remainder == null ? 0 : remainder.hashCode());
    }

    // this doesn't have a very precise meaning, just to reduce
    // noise from quotes in the rendered path for average cases
    static boolean hasFunkyChars(String s) {
        int length = s.length();

        if (length == 0)
            return false;

        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);

            if (Character.isLetterOrDigit(c) || c == '-' || c == '_')
                continue;
            else
                return true;
        }
        return false;
    }

    private void appendToStringBuilder(StringBuilder sb) {
        if (hasFunkyChars(first) || first.isEmpty())
            sb.append(ConfigImplUtil.renderJsonString(first));
        else
            sb.append(first);
        if (remainder != null) {
            sb.append(".");
            remainder.appendToStringBuilder(sb);
        }
    }

    String render() {
        StringBuilder sb = new StringBuilder();
        appendToStringBuilder(sb);
        return sb.toString();
    }
}
