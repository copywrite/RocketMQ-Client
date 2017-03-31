package com.copywrite.openservices.ons.api.impl;

import com.copywrite.openservices.ons.api.*;
import com.copywrite.openservices.ons.api.exception.ONSClientException;
import com.copywrite.openservices.ons.api.impl.rocketmq.ONSClientAbstract;
import com.copywrite.openservices.ons.api.impl.util.ONSUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by homikado on 17/2/22.
 */
public class ConsumerImpl extends ONSClientAbstract implements Consumer {
    private final DefaultMQPushConsumer defaultMQPushConsumer;
    private final ConcurrentHashMap<String, MessageListener> subscribeTable = new ConcurrentHashMap();
    private final AtomicBoolean started = new AtomicBoolean(false);

    public ConsumerImpl(Properties properties) {
        super(properties);
        this.defaultMQPushConsumer = new DefaultMQPushConsumer();
        String consumerGroup = properties.getProperty("ConsumerId");
        if(null == consumerGroup) {
            throw new ONSClientException("\'ConsumerId\' property is null");
        } else {
            String maxReconsumeTimes = properties.getProperty("maxReconsumeTimes");
            if(!UtilAll.isBlank(maxReconsumeTimes)) {
                try {
                    this.defaultMQPushConsumer.setMaxReconsumeTimes(Integer.parseInt(maxReconsumeTimes));
                } catch (NumberFormatException var8) {
                    ;
                }
            }

            String consumeTimeout = properties.getProperty("consumeTimeout");
            if(!UtilAll.isBlank(consumeTimeout)) {
                try {
                    this.defaultMQPushConsumer.setConsumeTimeout((long) Integer.parseInt(consumeTimeout));
                } catch (NumberFormatException var7) {
                    ;
                }
            }

            boolean isVipChannelEnabled = Boolean.parseBoolean(properties.getProperty("isVipChannelEnabled", "false"));
            this.defaultMQPushConsumer.setVipChannelEnabled(isVipChannelEnabled);
            String messageModel = properties.getProperty("MessageModel", "CLUSTERING");
            this.defaultMQPushConsumer.setMessageModel(MessageModel.valueOf(messageModel));
            this.defaultMQPushConsumer.setConsumerGroup(consumerGroup);
            this.defaultMQPushConsumer.setInstanceName(this.buildIntanceName());
            this.defaultMQPushConsumer.setNamesrvAddr(this.getNameServerAddr());
            if(properties.containsKey("ConsumeThreadNums")) {
                this.defaultMQPushConsumer.setConsumeThreadMin(Integer.valueOf(properties.get("ConsumeThreadNums").toString()).intValue());
                this.defaultMQPushConsumer.setConsumeThreadMax(Integer.valueOf(properties.get("ConsumeThreadNums").toString()).intValue());
            }

        }
    }

    public void start() {
        this.defaultMQPushConsumer.registerMessageListener(new ConsumerImpl.MessageListenerImpl());

        try {
            if(this.started.compareAndSet(false, true)) {
                this.defaultMQPushConsumer.start();
            }

        } catch (Exception var2) {
            throw new ONSClientException(var2.getMessage());
        }
    }

    public void shutdown() {
        if(this.started.compareAndSet(true, false)) {
            this.defaultMQPushConsumer.shutdown();
        }
    }

    public void subscribe(String topic, String subExpression, MessageListener listener) {
        if(null == topic) {
            throw new ONSClientException("topic is null");
        } else if(null == listener) {
            throw new ONSClientException("listener is null");
        } else {
            try {
                this.subscribeTable.put(topic, listener);
                this.defaultMQPushConsumer.subscribe(topic, subExpression);
            } catch (MQClientException var5) {
                throw new ONSClientException("defaultMQPushConsumer subscribe exception", var5);
            }
        }
    }

    public void unsubscribe(String topic) {
        if(null != topic) {
            this.defaultMQPushConsumer.unsubscribe(topic);
        }

    }

    public boolean isStarted() {
        return this.started.get();
    }

    public boolean isClosed() {
        return !this.isStarted();
    }

    class MessageListenerImpl implements MessageListenerConcurrently {
        MessageListenerImpl() {
        }

        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgsRMQList, ConsumeConcurrentlyContext contextRMQ) {
            MessageExt msgRMQ = (MessageExt)msgsRMQList.get(0);
            Message msg = ONSUtil.msgConvert(msgRMQ);
            Map stringStringMap = msgRMQ.getProperties();
            msg.setMsgID(msgRMQ.getMsgId());
            if(stringStringMap != null && stringStringMap.get("__transactionId__") != null) {
                msg.setMsgID((String)stringStringMap.get("__transactionId__"));
            }

            MessageListener listener = (MessageListener)ConsumerImpl.this.subscribeTable.get(msg.getTopic());
            if(null == listener) {
                throw new ONSClientException("MessageListener is null");
            } else {
                ConsumeContext context = new ConsumeContext();
                Action action = listener.consume(msg, context);
                if(action != null) {
                    switch (action) {
                        case CommitMessage:
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        case ReconsumeLater:
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        default:
                            break;
                    }
                }

                return null;
            }
        }
    }
}
