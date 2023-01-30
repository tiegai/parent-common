package com.nike.ncp.common.model.journey;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

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
    private List<FreqCtrlRes> freqCtrlRes;

    @Data
    public static class Trait {
        private String traitId;
        private String traitName;
        private String value;
    }

    @Data
    public static class FreqCtrlRes {
        private String tag;
        private String value;
    }
}
