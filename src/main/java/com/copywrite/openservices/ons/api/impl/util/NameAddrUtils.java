package com.copywrite.openservices.ons.api.impl.util;

/**
 * Created by homikado on 17/2/22.
 */
public class NameAddrUtils {
    public NameAddrUtils() {}

    public static String getNameAdd() {
        return PropertiesUtils.getProperty("rocketmq.namesrv.addr");
    }
}
