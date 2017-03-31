package com.copywrite.openservices.ons.api.impl;

import com.copywrite.openservices.ons.api.Consumer;
import com.copywrite.openservices.ons.api.ONSFactoryAPI;
import com.copywrite.openservices.ons.api.Producer;

import java.util.Properties;

/**
 * Created by homikado on 17/2/22.
 */
public class ONSFactoryImpl implements ONSFactoryAPI {

    public ONSFactoryImpl() {}

    public Producer createProducer(Properties properties) {
        ProducerImpl producer = new ProducerImpl(properties);
        return producer;
    }

    public Consumer createConsumer(Properties properties) {
        ConsumerImpl consumer = new ConsumerImpl(properties);
        return consumer;
    }
}
