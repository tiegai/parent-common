package com.nike.ncp.common.mongo.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page {
    private Long total;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private Integer current = 1;
    private Long pages;
    private String lastId;
}
