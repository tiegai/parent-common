package com.nike.ncp.common.service;

import com.nike.ncp.common.model.AuditLogEvent;
import com.nike.ncp.common.model.auditlog.OperTypeEnum;
import com.nike.ncp.common.model.auditlog.SysAuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditLogPublishService {
    @Value("${info.app.name}")
    private String appName;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private HttpServletRequest request;

    public void publishEvent(SysAuditLog auditLog) {
        applicationEventPublisher.publishEvent(new AuditLogEvent(this, auditLog));
        log.info("event=record_audit_log,Event-based publishing logging succeeded");
    }

    public void publishEvent(String resourceType, String description, OperTypeEnum operType) {
        SysAuditLog sysAuditLog = new SysAuditLog();
        sysAuditLog.setResourceType(resourceType);
        sysAuditLog.setAppName(appName);
        sysAuditLog.setOperType(operType.name());
        sysAuditLog.setDescription(description);
        sysAuditLog.setOperUser("SYSTEM");
        sysAuditLog.setAuditTime(LocalDateTime.now());
        sysAuditLog.setAction(request.getMethod());
        sysAuditLog.setOperUrl(request.getRequestURI());
        applicationEventPublisher.publishEvent(new AuditLogEvent(this, sysAuditLog));
        log.info("event=record_audit_log,Event-based publishing logging succeeded");
    }
}
