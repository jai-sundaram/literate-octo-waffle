package com.meridian.commerce.audit;

import com.meridian.commerce.security.AuthenticatedUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogs;

    public AuditAspect(AuditLogRepository auditLogs) {
        this.auditLogs = auditLogs;
    }

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        Object result = pjp.proceed();
        write(audited.value(), Arrays.toString(pjp.getArgs()));
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(String action, String details) {
        AuditLog log = new AuditLog();
        log.setActor(currentActor());
        log.setAction(action);
        log.setDetails(details);
        auditLogs.save(log);
    }

    private String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser u) {
            return u.email();
        }
        return "anonymous";
    }
}
