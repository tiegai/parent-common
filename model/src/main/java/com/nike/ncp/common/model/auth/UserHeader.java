package com.nike.ncp.common.model.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class UserHeader {
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";

    private String userId;
    private String userName;
}
