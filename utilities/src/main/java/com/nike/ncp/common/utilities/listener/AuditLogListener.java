package com.nike.ncp.common.utilities.listener;

import com.nike.ncp.common.mongo.service.MongoService;
import com.nike.ncp.common.utilities.model.AuditLogEvent;
import com.nike.ncp.common.utilities.model.SysAuditLog;
import com.nike.ncp.common.utilities.util.IsoDateUtil;
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
        sysAuditLog.setOperTime(IsoDateUtil.toUtc(LocalDateTime.now()));
        mongoService.insert(sysAuditLog);
    }
}
