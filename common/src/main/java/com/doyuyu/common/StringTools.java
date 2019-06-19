package com.doyuyu.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/23
 */
public class StringTools {
    public static List split(String str, String separatorChars){
        return Arrays.asList(StringUtils.split(str,separatorChars));
    }
}
