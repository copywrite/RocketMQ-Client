package com.copywrite.openservices.ons.api;

import java.io.Serializable;
import java.util.Properties;


/**
 * 消息类
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -1385924226856188094L;
    /**
     * 系统属性
     */
    Properties systemProperties;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 用户属性
     */
    private Properties userProperties;
    /**
     * 消息体
     */
    private byte[] body;

    public Message() {
        this(null, null, "", null);
    }


    public Message(String topic, String tag, String key, byte[] body) {
        this.topic = topic;
        this.body = body;

        this.putSystemProperties(SystemPropKey.TAG, tag);
        this.putSystemProperties(SystemPropKey.KEY, key);
    }

    void putSystemProperties(final String key, final String value) {
        if (null == this.systemProperties) {
            this.systemProperties = new Properties();
        }

        if (key != null && value != null) {
            this.systemProperties.put(key, value);
        }
    }


    public Message(String topic, String tags, byte[] body) {
        this(topic, tags, "", body);
    }

    public void putUserProperties(final String key, final String value) {
        if (null == this.userProperties) {
            this.userProperties = new Properties();
        }

        if (key != null && value != null) {
            this.userProperties.put(key, value);
        }
    }

    public String getUserProperties(final String key) {
        if (null != this.userProperties) {
            return (String) this.userProperties.get(key);
        }

        return null;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return this.getSystemProperties(SystemPropKey.TAG);
    }

    String getSystemProperties(final String key) {
        if (null != this.systemProperties) {
            return this.systemProperties.getProperty(key);
        }

        return null;
    }

    public void setTag(String tag) {
        this.putSystemProperties(SystemPropKey.TAG, tag);
    }

    public String getKey() {
        return this.getSystemProperties(SystemPropKey.KEY);
    }

    public void setKey(String key) {
        this.putSystemProperties(SystemPropKey.KEY, key);
    }

    public String getMsgID() {
        return this.getSystemProperties(SystemPropKey.MSGID);
    }

    public void setMsgID(String msgid) {
        this.putSystemProperties(SystemPropKey.MSGID, msgid);
    }

    Properties getSystemProperties() {
        return systemProperties;
    }

    void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }

    public Properties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Properties userProperties) {
        this.userProperties = userProperties;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getReconsumeTimes() {
        String pro = this.getSystemProperties(SystemPropKey.RECONSUMETIMES);
        if (pro != null) {
            return Integer.parseInt(pro);
        }

        return 0;
    }

    public void setReconsumeTimes(final int value) {
        putSystemProperties(SystemPropKey.RECONSUMETIMES, String.valueOf(value));
    }

    public long getStartDeliverTime() {
        String pro = this.getSystemProperties(SystemPropKey.STARTDELIVERTIME);
        if (pro != null) {
            return Long.parseLong(pro);
        }

        return 0;
    }

    /**
     * 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
     * <p>例1: 延迟投递, 延迟3s投递, 设置为: System.currentTimeMillis() + 3000;
     * <p>例2: 定时投递, 2016-02-01 11:30:00投递, 设置为: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-02-01 11:30:00").getTime()
     */
    public void setStartDeliverTime(final long value) {
        putSystemProperties(SystemPropKey.STARTDELIVERTIME, String.valueOf(value));
    }

    @Override
    public String toString() {
        return "Message [topic=" + topic + ", systemProperties=" + systemProperties + ", userProperties=" + userProperties + ", body="
                + (body != null ? body.length : 0) + "]";
    }

    static public class SystemPropKey {
        public static final String TAG = "__TAG";
        public static final String KEY = "__KEY";
        public static final String MSGID = "__MSGID";
        public static final String RECONSUMETIMES = "__RECONSUMETIMES";
        /**
         * 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
         * <p>例1: 延迟投递, 延迟3s投递, 设置为: System.currentTimeMillis() + 3000;
         * <p>例2: 定时投递, 2016-02-01 11:30:00投递, 设置为: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-02-01 11:30:00").getTime()
         */
        public static final String STARTDELIVERTIME = "__STARTDELIVERTIME";
    }
}
