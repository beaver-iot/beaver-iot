package com.milesight.iab.base.utils;

import com.milesight.iab.base.enums.EnumCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author leon
 */
public class EnumUtils {

    public static <E extends Enum> Map<String,String> getEnumMap(Class<E> enumClass){
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (final Enum e : enumClass.getEnumConstants()) {
            if(e instanceof EnumCode){
                EnumCode enumCode = (EnumCode) e;
                map.put(enumCode.getCode(), enumCode.getValue());
            }else{
                map.put(String.valueOf(e.ordinal()), e.name());
            }
        }
        return map;
    }

}