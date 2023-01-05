package com.nike.ncp.common.model;

import com.nike.ncp.common.model.auditlog.SysAuditLog;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class AuditLogEvent extends ApplicationEvent {

    private SysAuditLog sysAuditLog;

    public AuditLogEvent(Object source, SysAuditLog sysAuditLog) {
        super(source);
        this.sysAuditLog = sysAuditLog;
    }

    public AuditLogEvent(Object source, Clock clock, SysAuditLog sysAuditLog) {
        super(source, clock);
        this.sysAuditLog = sysAuditLog;
    }

    public SysAuditLog getSysAuditLog() {
        return sysAuditLog;
    }

    public void setSysAuditLog(SysAuditLog sysAuditLog) {
        this.sysAuditLog = sysAuditLog;
    }
}
