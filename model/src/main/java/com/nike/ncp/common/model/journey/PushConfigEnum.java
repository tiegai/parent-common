package com.nike.ncp.common.model.journey;

public enum PushConfigEnum {

    CHANNEL_NIKE_APP("NIKE_APP"),
    CHANNEL_SNKRS ("SNKRS"), // reserved for future

    MESSAGE_TYPE_INBOX_PUSH("INBOX_PUSH"),
    MESSAGE_TYPE_PUSH("PUSH"),
    MESSAGE_TYPE_INBOX("INBOX");

    private String name;

    PushConfigEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
