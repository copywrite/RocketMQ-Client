package com.copywrite.openservices.ons.api;

import java.util.Properties;

/**
 * Created by homikado on 17/2/22.
 */
public class ONSFactory {
    private static ONSFactoryAPI onsFactory = null;

    static {
        try {
            Class<?> factoryClass =
                    ONSFactory.class.getClassLoader().loadClass(
                            "com.copywrite.openservices.ons.api.impl.ONSFactoryImpl");
            onsFactory = (ONSFactoryAPI) factoryClass.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建Producer
     *
     * @param properties
     *         Producer的配置参数
     *
     * @return
     */
    public static Producer createProducer(final Properties properties) {
        return onsFactory.createProducer(properties);
    }

    /**
     * 创建Consumer
     *
     * @param properties
     *         Consumer的配置参数
     *
     * @return
     */
    public static Consumer createConsumer(final Properties properties) {
        return onsFactory.createConsumer(properties);
    }
}
