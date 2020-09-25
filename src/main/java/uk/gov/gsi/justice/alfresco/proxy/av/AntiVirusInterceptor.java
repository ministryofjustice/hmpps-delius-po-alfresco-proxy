package uk.gov.gsi.justice.alfresco.proxy.av;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.audit.UDInterchangeAuditLogService;
import uk.gov.gsi.justice.alfresco.proxy.audit.UDSPGLogFields;
import uk.gov.gsi.justice.alfresco.proxy.interceptor.UDLoggingInInterceptor;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.service.AlfrescoProxyHealthChecker;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampGenerator;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static uk.gov.gsi.justice.alfresco.proxy.av.AntivirusResponse.Status.ERROR;
import static uk.gov.gsi.justice.alfresco.proxy.av.AntivirusResponse.Status.FAILED;

public class AntiVirusInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiVirusInterceptor.class);
    public static final String SCANNING_FAILED = "AV scanning failed: ";
    public static final String SCANNING_ERROR = "There was an internal error while contacting/processing data from AV: ";
    public static final int VIRUS_FOUND_HTTP_CODE = 403;
    public static final int AV_ERROR_HTTP_CODE = 500;

    private AlfrescoProxyHealthChecker alfrescoProxyHealthChecker;

    private AntivirusClient antivirusClient;
    private UDInterchangeAuditLogService auditLogService;
    private boolean scanForViruses;

    public AntiVirusInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            final String healthInfo = alfrescoProxyHealthChecker.checkHealth();
            LOGGER.info("===============> {}", healthInfo);
        } catch (JsonProcessingException e) {
            LOGGER.error("Health Check Error::", e);
        }

        if (scanForViruses && message != null && message.getAttachments() != null) {
            for (Attachment attachment : message.getAttachments()) {
                try {
                    final ClamAvHealth clamAvHealth = antivirusClient.checkHealth();
                    LOGGER.info("=================> ClamAV Health:: {}", clamAvHealth);
                    checkForViruses(attachment.getDataHandler().getDataSource().getInputStream(), message);
                } catch (IOException e) {
                    throw new Fault(e);
                }
            }
        }
    }

    private void checkForViruses(InputStream is, Message message) throws IOException {
        final String fileName = "documents/eicar.txt";
        final InputStream fileAsStream = Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName))
                .orElseThrow(IOException::new);

        final AntivirusResponse antivirusResponse = antivirusClient.scan(fileAsStream);
        if (antivirusResponse.getStatus() == FAILED) {
            auditLogService.createUDAlertRecord(SCANNING_FAILED + antivirusResponse);

            logError(message, antivirusResponse.toString(), VIRUS_FOUND_HTTP_CODE);

            throw createFaultException(antivirusResponse, FORBIDDEN);
        } else if (antivirusResponse.getStatus() == ERROR) {
            auditLogService.createUDAlertRecord(SCANNING_ERROR + antivirusResponse);

            logError(message, antivirusResponse.toString(), AV_ERROR_HTTP_CODE);

            throw createFaultException(antivirusResponse, INTERNAL_SERVER_ERROR);
        }
    }

    void logError(Message message, String antivirusResponse, int code) {
        message.put(message.RESPONSE_CODE, code);
        message.put(UDSPGLogFields.UD_ERRDESC.toString(), antivirusResponse);
        String crn = getContentPayLoad(message);
        message.put(UDSPGLogFields.UD_CRN.toString(), crn);
        message.getExchange().remove(LoggingMessage.ID_KEY);
        message.remove(LoggingMessage.ID_KEY);
        UDLoggingInInterceptor interceptor = new UDLoggingInInterceptor();
        interceptor.setAuditLogService(new UDInterchangeAuditLogService());
        interceptor.setTimestampGenerator(new TimestampGenerator());
        interceptor.handleMessage(message);
    }

    private Fault createFaultException(AntivirusResponse avResponse, Response.Status status) {
        Fault fault = new Fault(avResponse.getException());
        fault.setStatusCode(status.getStatusCode());
        return fault;
    }

    public void setAlfrescoProxyHealthChecker(AlfrescoProxyHealthChecker alfrescoProxyHealthChecker) {
        this.alfrescoProxyHealthChecker = alfrescoProxyHealthChecker;
    }

    public void setAntivirusClient(AntivirusClient antivirusClient) {
        this.antivirusClient = antivirusClient;
    }

    public void setAuditLogService(UDInterchangeAuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    public void setScanForViruses(boolean scanForViruses) {
        this.scanForViruses = scanForViruses;
    }

    public String getContentPayLoad(Message message) {
        String soapMessage = null;
        try {
            List<MultipartBody> messageContentsList = message.getContent(List.class);
            if (messageContentsList.size() > 0) {
                MultipartBody body = messageContentsList.get(0);
                String crnId = body.getAttachmentObject("CRN", String.class);
/*              Also available are:
                org.apache.cxf.jaxrs.ext.multipart.Attachment document = body.getAttachment("filedata");
                String author = body.getAttachmentObject("author", String.class);
                String entityType = body.getAttachmentObject("entityType", String.class);
                String entityId = body.getAttachmentObject("entityId", String.class);
                String docType = body.getAttachmentObject("docType", String.class);*/
                soapMessage = crnId;
            }
        } catch (Exception e) {
            auditLogService.createUDAlertRecord("Problem extracting CRN from multipart: " + e);
        }

        return soapMessage == null ? "" : soapMessage;
    }
}
