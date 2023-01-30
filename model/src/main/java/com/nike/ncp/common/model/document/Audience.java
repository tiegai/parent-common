package com.nike.ncp.common.model.document;

import lombok.Data;

import java.util.List;

@Data
public class Audience {
    private String nextActivityId;
    private String upmid;
    private String nuid;
    private String phone;
    private String email;
    private List<Trait> traits;

    @Data
    public static class Trait {
        private String traitId;
        private String traitName;
        private String value;
    }
}
