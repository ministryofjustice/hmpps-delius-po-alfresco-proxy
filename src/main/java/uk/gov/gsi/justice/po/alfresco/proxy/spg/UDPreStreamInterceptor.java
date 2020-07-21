package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.audit.UDAuditLogBuilder;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.audit.UDInterchangeAuditLogService;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.utils.TimestampGenerator;

public class UDPreStreamInterceptor extends AbstractPhaseInterceptor<Message> {
    public UDPreStreamInterceptor() {
        super(Phase.PRE_STREAM);
    }


    private UDInterchangeAuditLogService auditLogService;
    private TimestampGenerator timestampGenerator;

    private org.slf4j.Logger log = LoggerFactory.getLogger(UDPreStreamInterceptor.class);

    @Override
    public void handleMessage(Message message) throws Fault {
        doLog(message);

    }

    protected void doLog(Message message) throws Fault {


        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        message.put(LoggingMessage.ID_KEY, id);

        String logText = UDAuditLogBuilder.createAuditLog("UDPreStreamInterceptor", timestampGenerator.getCurrentTimeStamp(), message);
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