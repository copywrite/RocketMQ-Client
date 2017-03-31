package com.copywrite.openservices.ons.api.impl;

import com.copywrite.openservices.ons.api.Message;
import com.copywrite.openservices.ons.api.Producer;
import com.copywrite.openservices.ons.api.SendResult;
import com.copywrite.openservices.ons.api.exception.ONSClientException;
import com.copywrite.openservices.ons.api.impl.rocketmq.FAQ;
import com.copywrite.openservices.ons.api.impl.rocketmq.ONSClientAbstract;
import com.copywrite.openservices.ons.api.impl.util.ONSUtil;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by homikado on 17/2/22.
 */
public class ProducerImpl extends ONSClientAbstract implements Producer{
    private final DefaultMQProducer defaultMQProducer;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public ProducerImpl(Properties properties) {
        super(properties);
        this.defaultMQProducer = new DefaultMQProducer();
        String producerGroup = properties.getProperty("ProducerId", "__ONS_PRODUCER_DEFAULT_GROUP");
        this.defaultMQProducer.setProducerGroup(producerGroup);
        if(properties.containsKey("SendMsgTimeoutMillis")) {
            this.defaultMQProducer.setSendMsgTimeout(Integer.valueOf(properties.get("SendMsgTimeoutMillis").toString()).intValue());
        } else {
            this.defaultMQProducer.setSendMsgTimeout(5000);
        }

        this.defaultMQProducer.setInstanceName(this.buildIntanceName());
        this.defaultMQProducer.setNamesrvAddr(this.getNameServerAddr());
        this.defaultMQProducer.setMaxMessageSize(4194304);
    }

    public void start() {
        try {
            if(this.started.compareAndSet(false, true)) {
                this.defaultMQProducer.start();
            }

        } catch (Exception var2) {

        }
    }

    public void shutdown() {
        if(this.started.compareAndSet(true, false)) {
            this.defaultMQProducer.shutdown();
        }
    }

    public SendResult send(Message message) {
        this.checkONSProducerServiceState(this.defaultMQProducer.getDefaultMQProducerImpl());
        org.apache.rocketmq.common.message.Message msgRMQ = ONSUtil.msgConvert(message);

        try {
            org.apache.rocketmq.client.producer.SendResult e = this.defaultMQProducer.send(msgRMQ);
            SendResult sendResult = new SendResult();
            sendResult.setMessageId(e.getMsgId());
            message.setMsgID(e.getMsgId());
            return sendResult;
        } catch (Exception var5) {
            this.checkProducerException(var5, message);
            return null;
        }
    }

    private void checkProducerException(Exception e, Message message) {
        if(e instanceof MQClientException) {
            if(e.getCause() != null) {
                if(e.getCause() instanceof RemotingConnectException) {
                    throw new ONSClientException(FAQ.errorMessage(String.format("Connect broker failed, Topic: %s", new Object[]{message.getTopic()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&connect_broker_failed"));
                }

                if(e.getCause() instanceof RemotingTimeoutException) {
                    throw new ONSClientException(FAQ.errorMessage(String.format("Send message to broker timeout, %dms, Topic: %s", new Object[]{Integer.valueOf(this.defaultMQProducer.getSendMsgTimeout()), message.getTopic()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&send_msg_failed"));
                }

                if(e.getCause() instanceof MQBrokerException) {
                    MQBrokerException excep = (MQBrokerException)e.getCause();
                    throw new ONSClientException(FAQ.errorMessage(String.format("Receive a broker exception, Topic: %s, %s", new Object[]{message.getTopic(), excep.getErrorMessage()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&broker_response_exception"));
                }
            } else {
                MQClientException excep1 = (MQClientException)e;
                if(-1 == excep1.getResponseCode()) {
                    throw new ONSClientException(FAQ.errorMessage(String.format("Topic does not exist, Topic: %s", new Object[]{message.getTopic()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&topic_not_exist"));
                }

                if(13 == excep1.getResponseCode()) {
                    throw new ONSClientException(FAQ.errorMessage(String.format("ONS Client check message exception, Topic: %s", new Object[]{message.getTopic()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&msg_check_failed"));
                }
            }
        }

        throw new ONSClientException("defaultMQProducer send exception", e);
    }

    public void sendOneway(Message message) {
        
    }
}
