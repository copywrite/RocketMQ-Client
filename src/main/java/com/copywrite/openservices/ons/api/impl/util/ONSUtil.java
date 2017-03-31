package com.copywrite.openservices.ons.api.impl.util;

import com.copywrite.openservices.ons.api.MessageAccessor;
import com.copywrite.openservices.ons.api.exception.ONSClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.*;

/**
 * Created by homikado on 17/2/22.
 */
public class ONSUtil {
    private static Set<String> ReservedKeySetRMQ = new HashSet();
    private static Set<String> ReservedKeySetONS = new HashSet();

    public ONSUtil() {
    }

    public static Message msgConvert(com.copywrite.openservices.ons.api.Message message) {
        Message msgRMQ = new Message();
        if(message == null) {
            throw new ONSClientException("\'message\' is null");
        } else {
            if(message.getTopic() != null) {
                msgRMQ.setTopic(message.getTopic());
            }

            if(message.getKey() != null) {
                msgRMQ.setKeys(message.getKey());
            }

            if(message.getTag() != null) {
                msgRMQ.setTags(message.getTag());
            }

            if(message.getStartDeliverTime() > 0L) {
                msgRMQ.putUserProperty("__STARTDELIVERTIME", String.valueOf(message.getStartDeliverTime()));
            }

            if(message.getBody() != null) {
                msgRMQ.setBody(message.getBody());
            }

            Properties systemProperties = MessageAccessor.getSystemProperties(message);
            if(systemProperties != null) {
                Iterator userProperties = systemProperties.entrySet().iterator();

                while(userProperties.hasNext()) {
                    Map.Entry it = (Map.Entry)userProperties.next();
                    if(!ReservedKeySetONS.contains(it.getKey().toString())) {
                        org.apache.rocketmq.common.message.MessageAccessor.putProperty(msgRMQ, it.getKey().toString(), it.getValue().toString());
                    }
                }
            }

            Properties userProperties1 = message.getUserProperties();
            if(userProperties1 != null) {
                Iterator it1 = userProperties1.entrySet().iterator();

                while(it1.hasNext()) {
                    Map.Entry next = (Map.Entry)it1.next();
                    if(!ReservedKeySetRMQ.contains(next.getKey().toString())) {
                        org.apache.rocketmq.common.message.MessageAccessor.putProperty(msgRMQ, next.getKey().toString(), next.getValue().toString());
                    }
                }
            }

            return msgRMQ;
        }
    }

    public static com.copywrite.openservices.ons.api.Message msgConvert(Message msgRMQ) {
        com.copywrite.openservices.ons.api.Message message = new com.copywrite.openservices.ons.api.Message();
        if(msgRMQ.getTopic() != null) {
            message.setTopic(msgRMQ.getTopic());
        }

        if(msgRMQ.getKeys() != null) {
            message.setKey(msgRMQ.getKeys());
        }

        if(msgRMQ.getTags() != null) {
            message.setTag(msgRMQ.getTags());
        }

        if(msgRMQ.getBody() != null) {
            message.setBody(msgRMQ.getBody());
        }

        message.setReconsumeTimes(((MessageExt)msgRMQ).getReconsumeTimes());
        Map properties = msgRMQ.getProperties();
        if(properties != null) {
            Iterator it = properties.entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry next = (Map.Entry)it.next();
                if(ReservedKeySetRMQ.contains(((String)next.getKey()).toString())) {
                    MessageAccessor.putSystemProperties(message, ((String)next.getKey()).toString(), ((String)next.getValue()).toString());
                } else {
                    message.putUserProperties(((String)next.getKey()).toString(), ((String)next.getValue()).toString());
                }
            }
        }

        return message;
    }

    static {
        ReservedKeySetRMQ.add("KEYS");
        ReservedKeySetRMQ.add("TAGS");
        ReservedKeySetRMQ.add("WAIT");
        ReservedKeySetRMQ.add("DELAY");
        ReservedKeySetRMQ.add("RETRY_TOPIC");
        ReservedKeySetRMQ.add("REAL_TOPIC");
        ReservedKeySetRMQ.add("REAL_QID");
        ReservedKeySetRMQ.add("TRAN_MSG");
        ReservedKeySetRMQ.add("PGROUP");
        ReservedKeySetRMQ.add("MIN_OFFSET");
        ReservedKeySetRMQ.add("MAX_OFFSET");
        ReservedKeySetONS.add("__TAG");
        ReservedKeySetONS.add("__KEY");
        ReservedKeySetONS.add("__MSGID");
        ReservedKeySetONS.add("__RECONSUMETIMES");
        ReservedKeySetONS.add("__STARTDELIVERTIME");
    }
}
