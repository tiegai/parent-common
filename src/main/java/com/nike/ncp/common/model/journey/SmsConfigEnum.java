package com.nike.ncp.common.model.journey;

public enum SmsConfigEnum {

    SMS_VENDOR_ZHUJI("ZHUJI");

    private String name;

    SmsConfigEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
