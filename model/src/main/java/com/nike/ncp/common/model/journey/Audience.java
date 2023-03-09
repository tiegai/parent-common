package com.nike.ncp.common.model.journey;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
public class Audience {
    @Id
    private String id;
    private String nextActivityId;
    private String upmid;
    private String nuid;
    private String phone;
    private String email;
    private List<Trait> traits;
    private Map<String, String> freqCtrlRes;

    @Data
    public static class Trait {
        private String traitId;
        private String traitName;
        private String value;
    }
}
