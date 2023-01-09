package com.nike.ncp.common.model.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class Page {
    @ApiModelProperty
    private Long total;
    @Builder.Default
    @ApiModelProperty
    private Integer size = 20;
    @Builder.Default
    @ApiModelProperty
    private Integer current = 1;
    @ApiModelProperty
    private Long pages;
    @ApiModelProperty
    private String lastId;
}
