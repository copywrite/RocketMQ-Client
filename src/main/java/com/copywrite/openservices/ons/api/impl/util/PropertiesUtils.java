package com.copywrite.openservices.ons.api.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by homikado on 17/2/28.
 */
public class PropertiesUtils {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private static Properties props = null;

    static{

        try {
            props = new Properties();
            InputStream in =  PropertiesUtils.class.getClassLoader().getResourceAsStream("variables.properties");
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            props.load(bf);
        } catch (Exception e) {
            logger.error("read file variables.properties error", e);
        }
    }

    private PropertiesUtils() {}

    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}
