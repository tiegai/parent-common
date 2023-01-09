package com.nike.ncp.common.model.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class PageResp<T> {

    @ApiModelProperty
    private Page page;

    @Builder.Default
    @ApiModelProperty
    private List<T> data = Collections.emptyList();
}
