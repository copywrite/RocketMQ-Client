package com.copywrite.openservices.ons.api;

/**
 * 消息生产者接口
 */
public interface Producer {
    /**
     * 启动服务
     */
    void start();


    /**
     * 关闭服务
     */
    void shutdown();


    /**
     * 同步发送消息，只要不抛异常就表示成功
     *
     * @param message
     *
     * @return 发送结果，含消息Id
     */
    SendResult send(final Message message);


    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param message
     */
    void sendOneway(final Message message);
}
