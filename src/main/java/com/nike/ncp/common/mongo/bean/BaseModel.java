package com.nike.ncp.common.mongo.bean;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BaseModel implements Serializable {
    @Id
    private String id;
    @CreateTime
    private LocalDateTime createTime;
    @UpdateTime
    private LocalDateTime updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
