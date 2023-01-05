package com.nike.ncp.common.service;

import com.nike.ncp.common.model.AuditLogEvent;
import com.nike.ncp.common.model.auditlog.SysAuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditLogPublishService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(SysAuditLog auditLog) {
        applicationEventPublisher.publishEvent(new AuditLogEvent(this, auditLog));
        log.info("event=record_audit_log,Event-based publishing logging succeeded");
    }
}
