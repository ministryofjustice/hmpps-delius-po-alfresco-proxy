package uk.gov.gsi.justice.po.alfresco.proxy.spg.audit;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class UDInterchangeAuditLogService {

    private static final String AUDIT_SERVICE_INITIALISATION_MSG = "Initialising SPGInterchange Log Service";
    private static final String AUDIT_SERVICE_DESTROY_MSG = "Shutting Down Audit Logging Service";
    private static final String SPG_UD_AUDITLOG_AUDIT_LEVEL_LOGNAME = UDAuditLog4jLevel.class.getName();
    private static final String SPG_UD_AUDITLOG_ALERT_LEVEL_LOGNAME = UDAlertLog4jLevel.class.getName();
    private static final String SPG_UD_AUDITLOG_SECURITY_LEVEL_LOGNAME = UDSecurityLog4jLevel.class.getName();
    private static final String SPG_AUDITLOG_AUDIT_SERVICE_LOGNAME = UDInterchangeAuditLogService.class.getName();

    private Logger logService = Logger.getLogger(SPG_AUDITLOG_AUDIT_SERVICE_LOGNAME);
    private Logger unstructuredAuditlog = Logger.getLogger(SPG_UD_AUDITLOG_AUDIT_LEVEL_LOGNAME);
    private Logger unstructuredSecuritylog = Logger.getLogger(SPG_UD_AUDITLOG_SECURITY_LEVEL_LOGNAME);
    private Logger unstructuredAlertlog = Logger.getLogger(SPG_UD_AUDITLOG_ALERT_LEVEL_LOGNAME);

    public void createUDAuditRecord(String logRecord) {
        logService.log(UDAuditLog4jLevel.INFO, logRecord);//log to main log
        unstructuredAuditlog.log(UDAuditLog4jLevel.AUDIT, "AUDIT " + logRecord);//log to audit log appender
    }
    
    public void createUDSecurityAuditRecord(String logRecord) {
            logService.log(UDAuditLog4jLevel.INFO, logRecord);//log to main log
            unstructuredSecuritylog.log(UDSecurityLog4jLevel.SECURITY, logRecord);//log to audit log appender
    }

    public void createUDAlertRecord(String logRecord) {
            logService.log(UDAuditLog4jLevel.WARN, logRecord);//log to main log
            unstructuredAlertlog.log(UDAlertLog4jLevel.ALERT, "ALERT " + logRecord);//log to audit log appender
    }

    @PostConstruct
    void afterPropertiesSet() {
        logService.info(AUDIT_SERVICE_INITIALISATION_MSG);
    }

    @PreDestroy
    private void destroy() {
        logService.info(AUDIT_SERVICE_DESTROY_MSG);
    }
}
