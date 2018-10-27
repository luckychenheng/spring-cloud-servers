package com.spring.cloud.auth.common.util;

/**
 * @author wangmj
 * @since 2018/10/27
 */
public class StringHelper {
    public static String getObjectValue(Object obj){
        return obj==null?"":obj.toString();
    }
}
