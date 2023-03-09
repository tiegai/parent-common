package com.nike.ncp.common.utilities.constant;

public final class Constants {
    public static final String ID = "id";
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "userName";
    public static final String AUDIENCE_TABLE_PREFIX = "audience_";

    private Constants() {
        throw new IllegalStateException(this.getClass().getName()
                + "is an utility class and should not have public or default constructors.");
    }
}
