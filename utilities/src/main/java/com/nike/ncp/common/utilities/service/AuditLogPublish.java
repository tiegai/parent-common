package com.nike.ncp.common.utilities.service;

import com.nike.ncp.common.utilities.constant.Constants;
import com.nike.ncp.common.utilities.model.AuditLogEvent;
import com.nike.ncp.common.utilities.model.OperTypeEnum;
import com.nike.ncp.common.utilities.model.ResourceTypeEnum;
import com.nike.ncp.common.utilities.model.SysAuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class AuditLogPublish {
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

    public void publishEvent(ResourceTypeEnum resourceType, String description, OperTypeEnum operType) {
        SysAuditLog sysAuditLog = new SysAuditLog();
        sysAuditLog.setResourceType(resourceType.value());
        sysAuditLog.setAppName(appName);
        sysAuditLog.setOperType(operType.name());
        sysAuditLog.setDescription(description);
        sysAuditLog.setOperBy(request.getHeader(Constants.USER_EMAIL));
        sysAuditLog.setOperByName(request.getHeader(Constants.USER_NAME));
        sysAuditLog.setAction(request.getMethod());
        sysAuditLog.setOperUrl(request.getRequestURI());
        applicationEventPublisher.publishEvent(new AuditLogEvent(this, sysAuditLog));
        log.info("event=record_audit_log,Event-based publishing logging succeeded");
    }
}
