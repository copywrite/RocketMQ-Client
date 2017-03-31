package com.copywrite.openservices.ons.api.impl.rocketmq;

import com.copywrite.openservices.ons.api.exception.ONSClientException;
import com.copywrite.openservices.ons.api.impl.util.NameAddrUtils;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.common.ServiceState;
import org.apache.rocketmq.common.UtilAll;

import java.util.Properties;

/**
 * Created by homikado on 17/2/22.
 */
public abstract class ONSClientAbstract {
    protected final Properties properties;
    protected String nameServerAddr = NameAddrUtils.getNameAdd();

    public ONSClientAbstract(Properties properties) {
        this.properties = properties;
        if(null == this.nameServerAddr) {
            throw new ONSClientException(FAQ.errorMessage("Can not find name server, May be your network problem.", "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&namesrv_not_exist"));
        }
    }

    protected String buildIntanceName() {
        return Integer.toString(UtilAll.getPid()) + "#" + this.nameServerAddr.hashCode() + "#" + System.nanoTime();
    }

    public String getNameServerAddr() {
        return this.nameServerAddr;
    }

    protected void checkONSProducerServiceState(DefaultMQProducerImpl producer) {
        switch(ServiceState.values()[producer.getServiceState().ordinal()]) {
            case CREATE_JUST:
                throw new ONSClientException(FAQ.errorMessage(String.format("You do not have start the producer[" + UtilAll.getPid() + "], %s", new Object[]{producer.getServiceState()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&service_not_ok"));
            case SHUTDOWN_ALREADY:
                throw new ONSClientException(FAQ.errorMessage(String.format("Your producer has been shut down, %s", new Object[]{producer.getServiceState()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&service_not_ok"));
            case START_FAILED:
                throw new ONSClientException(FAQ.errorMessage(String.format("When you start your service throws an exception, %s", new Object[]{producer.getServiceState()}), "http://docs.aliyun.com/cn#/pub/ons/faq/exceptions&service_not_ok"));
            case RUNNING:
            default:
        }
    }
}
