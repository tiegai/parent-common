package com.nike.ncp.common.mongo.bean;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BaseModel implements Serializable {
    @Id
    private String id;
    @CreatedTime
    private LocalDateTime createdTime;
    @UpdatedTime
    private LocalDateTime updatedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
