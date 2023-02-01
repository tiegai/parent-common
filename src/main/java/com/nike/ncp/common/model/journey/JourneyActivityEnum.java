package com.nike.ncp.common.model.journey;

public enum JourneyActivityEnum {

    CATEGORY_START(ActivityCategoryEnum.START.value()),
    CATEGORY_END(ActivityCategoryEnum.END.value()),
    CATEGORY_AUDIENCE(ActivityCategoryEnum.AUDIENCE.value()),
    CATEGORY_TRAIT_CHOICE(ActivityCategoryEnum.TRAIT_CHOICE.value()),
    CATEGORY_RANDOM_CHOICE(ActivityCategoryEnum.RANDOM_CHOICE.value()),
    CATEGORY_SMS(ActivityCategoryEnum.SMS.value()),
    CATEGORY_PUSH(ActivityCategoryEnum.PUSH.value()),
    CATEGORY_EMAIL(ActivityCategoryEnum.EMAIL.value()),
    CATEGORY_INBOX(ActivityCategoryEnum.INBOX.value()),
    CATEGORY_TAG(ActivityCategoryEnum.TAG.value()),
    CATEGORY_WAIT(ActivityCategoryEnum.WAIT.value());

    private String name;

    JourneyActivityEnum(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
