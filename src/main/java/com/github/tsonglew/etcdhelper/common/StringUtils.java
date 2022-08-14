package com.github.tsonglew.etcdhelper.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tsonglew
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    public static final String SLASH = "/";
    public static final String NEWLINE = "\n";
    public static final String ESCAPED_NEWLINE = "\\n";
    public static final String EMPTY = "";
    public static final String UNDERSCORE = "_";
    public static final String AT = "@";
    public static final String HYPHEN = "-";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String DOT = ".";
    public static final String ELLIPSIS = "...";
    public static final String EMPTY_MAP = "{}";
    public static final String ZERO = "0";
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    @NotNull
    private static String join(@Nullable String delimiter, String... parts) {
        return Arrays.stream(parts)
                .map(p -> null == p ? EMPTY : p)
                .collect(Collectors.joining(null == delimiter ? EMPTY : delimiter));
    }

    @NotNull
    @Contract(value = "null -> new", pure = true)
    public static String[] hyphenSplit(@Nullable String s) {
        if (null == s) {
            return new String[0];
        }
        return s.split(HYPHEN);
    }

    @NotNull
    public static String hyphenJoin(String... parts) {
        return join(HYPHEN, parts);
    }

    @NotNull
    public static String underscoreJoin(String... parts) {
        return join(UNDERSCORE, parts);
    }

    @NotNull
    public static String slashJoin(String... parts) {
        return join(SLASH, parts);
    }

    @NotNull
    @Contract(pure = true)
    public static String[] slashSplit(@Nullable String s) {
        if (null == s) {
            return new String[0];
        }
        return s.split(SLASH);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static String bytes2String(byte[] bytes) {
        return new String(bytes, CHARSET);
    }

    @NotNull
    public static byte[] string2Bytes(@Nullable String s) {
        return s == null ? EMPTY.getBytes(CHARSET) : s.getBytes(CHARSET);
    }

    @NotNull
    @Contract("null -> new")
    public static String[] newlineSplit(@Nullable Object s) {
        if (null == s) {
            return new String[0];
        }
        return s.toString().split(NEWLINE);
    }

    @NotNull
    public static String escapedNewlineJoin(String... parts) {
        return join(ESCAPED_NEWLINE, parts);
    }

    @NotNull
    @Contract(pure = true)
    public static String[] colonSplit(@Nullable String s) {
        if (null == s) {
            return new String[0];
        }
        return s.split(COLON);
    }

    @NotNull
    public static String colonJoin(String... parts) {
        return join(COLON, parts);
    }

    @NotNull
    public static String compress(@Nullable Object s) {
        if (null == s) {
            return EMPTY;
        }
        return s.toString().replace(NEWLINE, ESCAPED_NEWLINE);
    }

    @NotNull
    public static String trim(@Nullable Object o, int size) {
        if (null == o) {
            return EMPTY;
        }
        String s = o.toString();
        return s.substring(0, Math.min(s.length(), size)) + (o.toString().length() > size ? ELLIPSIS : EMPTY);
    }

    @NotNull
    public static String trimLines(@Nullable Object o, int size) {
        if (null == o) {
            return EMPTY;
        }
        return escapedNewlineJoin(Arrays.stream(newlineSplit(o)).map(s -> trim(s, size)).toArray(String[]::new));
    }

    @NotNull
    private static String splitLastPart(@Nullable String s, @Nullable String splitter) {
        if (null == s) {
            return EMPTY;
        }
        if (null == splitter) {
            return s;
        }
        return s.substring(s.lastIndexOf(splitter) + 1);
    }

    @NotNull
    public static String slashSplitLastPart(@Nullable String s) {
        return splitLastPart(s, SLASH);
    }

    @NotNull
    public static String hyphenSplitLastPart(@Nullable String s) {
        return splitLastPart(s, HYPHEN);
    }

}

