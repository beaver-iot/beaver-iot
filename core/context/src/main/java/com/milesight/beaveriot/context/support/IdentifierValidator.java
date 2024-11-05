package com.milesight.beaveriot.context.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leon
 */
public class IdentifierValidator {

    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9_@#$\\-\\/]+$");

    private IdentifierValidator(){
    }

    public static boolean isValid(String identifier) {
        Assert.notNull(identifier, "identifier must not be null");
        Matcher matcher = pattern.matcher(identifier);
        if(!matcher.matches()){
            throw new IllegalArgumentException("identifier must be a string that matches the pattern ^[A-Za-z0-9_@#$\\-]+$ :" + identifier);
        }
        return true;
    }

    public static boolean isValidNullable(String identifier) {
        return (StringUtils.hasText(identifier)) ? isValid(identifier) : true;
    }

}
