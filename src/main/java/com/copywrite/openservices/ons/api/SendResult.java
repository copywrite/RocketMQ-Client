package com.copywrite.openservices.ons.api;

public class SendResult {
    private String messageId;


    public String getMessageId() {
        return messageId;
    }


    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    @Override
    public String toString() {
        return "SendResult{" +
                "messageId='" + messageId + '\'' +
                '}';
    }
}
