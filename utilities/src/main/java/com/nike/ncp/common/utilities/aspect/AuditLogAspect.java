package com.nike.ncp.common.utilities.aspect;

import com.nike.ncp.common.utilities.constant.Constants;
import com.nike.ncp.common.utilities.model.AuditLog;
import com.nike.ncp.common.utilities.model.AuditLogEvent;
import com.nike.ncp.common.utilities.model.SysAuditLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class AuditLogAspect {

    @Value("${info.app.name}")
    private String appName;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Pointcut("@annotation(com.nike.ncp.common.utilities.model.AuditLog)")
    public void auditLog() {
    }

    @After("auditLog()")
    public void doAfter(JoinPoint joinPoint) {
        AuditLog auditLog = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(AuditLog.class);
        SysAuditLog sysAuditLog = new SysAuditLog();
        sysAuditLog.setResourceType(auditLog.resourceType().value());
        sysAuditLog.setAppName(appName);
        sysAuditLog.setOperType(auditLog.operType().name());
        sysAuditLog.setDescription(auditLog.description());
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) attributes;
            HttpServletRequest request = requestAttributes.getRequest();
            sysAuditLog.setAction(request.getMethod());
            sysAuditLog.setOperUrl(request.getRequestURI());
            sysAuditLog.setOperBy(request.getHeader(Constants.USER_EMAIL));
            sysAuditLog.setOperByName(request.getHeader(Constants.USER_NAME));
        }
        applicationEventPublisher.publishEvent(new AuditLogEvent(this, sysAuditLog));
        log.info("event=record_audit_log, The annotation @AuditLog record log to succeeded");
    }
}
