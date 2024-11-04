package com.milesight.beaveriot.base.utils;

import com.milesight.beaveriot.base.constants.StringConstant;

/**
 * @author leon
 */
public class StringUtils {

    private StringUtils(){
    }
    /**
     * Convert camelCase-named strings to underscores, also known as SnakeCase and underScoreCase. <br>
     * If the camelCase named string before conversion is empty, an empty string is returned. <br>
     *The rules are:
     * <ul>
     * <li>Words are separated by underscores</li>
     * <li>Also use lowercase letters for the first letter of each word</li>
     * </ul>
     * For example:
     *
     * <pre>
     *HelloWorld=》hello_world
     *Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str The camel case named string before conversion, which can also be in underscore form
     * @return The converted string named with underscores
     */
    public static String toSnakeCase(CharSequence str) {
        return toSymbolCase(str, StringConstant.UNDERLINE.charAt(0));
    }


    /**
     * Peak-style named strings are converted to symbolic links. If the camelCase named string before conversion is empty, an empty string is returned.
     *
     * @param str The camel case named string before conversion, which can also be in the form of a symbolic connection
     * @param symbol connector
     * @return the converted symbolic link named string
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;

                if (null != preChar) {
                    if (symbol == preChar) {
                        if (null == nextChar || Character.isLowerCase(nextChar)) {
                            c = Character.toLowerCase(c);
                        }
                    } else if (Character.isLowerCase(preChar)) {
                        sb.append(symbol);
                        if (null == nextChar || Character.isLowerCase(nextChar) || isNumber(nextChar)) {
                            c = Character.toLowerCase(c);
                        }
                    } else {
                        if (null != nextChar && Character.isLowerCase(nextChar)) {
                            sb.append(symbol);
                            c = Character.toLowerCase(c);
                        }
                    }
                } else {
                    if (null == nextChar || Character.isLowerCase(nextChar)) {
                        c = Character.toLowerCase(c);
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Converts underscore-named strings to camelCase. If the underscore-capitalized string before conversion is empty, an empty string is returned. <br>
     *The rules are:
     * <ul>
     * <li>Do not separate words with spaces or any connectors</li>
     * <li>The first letter of the first word is lowercase letters</li>
     * <li>Also use capital letters for the first letters of subsequent words</li>
     * </ul>
     * For example: hello_world=》helloWorld
     *
     * @param name String named in underline capitalization before conversion
     * @return the converted camel case named string
     */
    public static String toCamelCase(CharSequence name) {
        return toCamelCase(name, StringConstant.UNDERLINE.charAt(0));
    }

    /**
     *Convert a concatenated string to camelCase. If the underscore-capitalized string before conversion is empty, an empty string is returned.
     *
     * @param name A string named in a custom way before conversion
     * @param symbol connector in the original string
     * @return the converted camel case named string
     */
    public static String toCamelCase(CharSequence name, char symbol) {
        if (null == name) {
            return null;
        }

        final String name2 = name.toString();
        if (name2.indexOf(symbol) != -1) {
            final int length = name2.length();
            final StringBuilder sb = new StringBuilder(length);
            boolean upperCase = false;
            for (int i = 0; i < length; i++) {
                char c = name2.charAt(i);

                if (c == symbol) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

}
