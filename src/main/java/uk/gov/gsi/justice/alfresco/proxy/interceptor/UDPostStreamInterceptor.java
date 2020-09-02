package uk.gov.gsi.justice.alfresco.proxy.interceptor;

import org.apache.cxf.interceptor.*;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.audit.UDAuditLogBuilder;
import uk.gov.gsi.justice.alfresco.proxy.audit.UDInterchangeAuditLogService;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampGenerator;

import java.util.Date;

public class UDPostStreamInterceptor extends AbstractPhaseInterceptor<Message> {
    private Logger log = LoggerFactory.getLogger(UDPostStreamInterceptor.class);
    private UDInterchangeAuditLogService auditLogService;
    private TimestampGenerator timestampGenerator;

    public UDPostStreamInterceptor() {
        super(Phase.POST_STREAM);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        doLog(message);
    }

    protected void doLog(Message message) throws Fault {
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        message.put(LoggingMessage.ID_KEY, id);

        Date created = (Date) message.getExchange().get(UDLoggingInInterceptor.SPG_INBOUND_TIMESTAMP);
        // calculate elapsed time
        long exchangeTotalTimeMillis=0;
        if (created != null) {
            Date now = new Date();
            exchangeTotalTimeMillis = now.getTime() - created.getTime();

        }
		log.info(">>> Took " + exchangeTotalTimeMillis + " millis for the exchange on the route ");

        String logText = UDAuditLogBuilder.createAuditLog("UDPostStreamInterceptor", timestampGenerator.getCurrentTimeStamp(), message, exchangeTotalTimeMillis);
        auditLogService.createUDAuditRecord(logText);
    }

    public UDInterchangeAuditLogService getAuditLogService() {
        return auditLogService;
    }

    public void setAuditLogService(UDInterchangeAuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    public TimestampGenerator getTimestampGenerator() {
        return timestampGenerator;
    }

    public void setTimestampGenerator(TimestampGenerator timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
    }
}