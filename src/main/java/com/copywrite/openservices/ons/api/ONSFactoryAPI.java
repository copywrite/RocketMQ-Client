package com.copywrite.openservices.ons.api;

import java.util.Properties;

public interface ONSFactoryAPI {
    Producer createProducer(final Properties properties);

    Consumer createConsumer(final Properties properties);
}
