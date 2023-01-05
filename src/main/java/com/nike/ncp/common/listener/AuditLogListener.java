package com.nike.ncp.common.listener;

import com.nike.ncp.common.model.AuditLogEvent;
import com.nike.ncp.common.model.auditlog.SysAuditLog;
import com.nike.ncp.common.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogListener {

    @Autowired
    private MongoService mongoService;

    @Async
    @EventListener
    public void handleAuditLog(AuditLogEvent logEvent) {
        SysAuditLog sysAuditLog = logEvent.getSysAuditLog();
        sysAuditLog.setAuditTime(LocalDateTime.now());
        mongoService.insert(sysAuditLog);
    }
}
