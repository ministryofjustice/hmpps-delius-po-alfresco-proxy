package uk.gov.gsi.justice.spg;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.spg.audit.*;
import uk.gov.gsi.justice.spg.utils.TimestampGenerator;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.logging.Logger;

public class UDLoggingInInterceptor extends LoggingInInterceptor {

	private UDInterchangeAuditLogService auditLogService;
	private TimestampGenerator timestampGenerator;
	static  private String headerBlacklist;
	private boolean displayPayload;

	public static String SPG_INBOUND_TIMESTAMP="SPG_INBOUND_TIMESTAMP";

    private org.slf4j.Logger log = LoggerFactory.getLogger(UDLoggingInInterceptor.class);

    @Override
	public void handleMessage(Message message) throws Fault {
		super.handleMessage(message);
	}

    @Override
    protected void logging(Logger logger, Message message) throws Fault {

        if (message.containsKey(LoggingMessage.ID_KEY)) {
            return;
        }

        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }

        message.put(LoggingMessage.ID_KEY, id);

        message.getExchange().put(SPG_INBOUND_TIMESTAMP,new Date());

        final LoggingMessage buffer = new LoggingMessage("Inbound Message", id);

        if (!Boolean.TRUE.equals(message.get(Message.DECOUPLED_CHANNEL_MESSAGE))) {
            // avoid logging the default responseCode 200 for the decoupled
            // responses

            Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
            if (responseCode != null) {
                buffer.getResponseCode().append(responseCode);
            }
        }

        String encoding = (String) message.get(Message.ENCODING);

        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }

        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }

        String contentType = (String) message.get(Message.CONTENT_TYPE);
        if (contentType != null) {
            buffer.getContentType().append(contentType);
        }

        Map<String,List> headerMap = (Map<String, List>)message.get(Message.PROTOCOL_HEADERS);
        if (headerMap != null) {
            List<String> blacklist = new ArrayList<>(Arrays.asList(headerBlacklist.split(",")));
            for (Map.Entry<String, List> entry : headerMap.entrySet()) {
                if(!blacklist.contains(entry.getKey())){
                    buffer.getHeader().append(entry).append(" ");
                }
            }
        }

        String uri = (String) message.get(Message.REQUEST_URL);
        if (uri == null) {
            String address = (String) message.get(Message.ENDPOINT_ADDRESS);
            uri = (String) message.get(Message.REQUEST_URI);
            if (uri != null && uri.startsWith("/")) {
                if (address != null && !address.startsWith(uri)) {
                    if (address.endsWith("/") && address.length() > 1) {
                        address = address.substring(0, address.length());
                    }
                    uri = address + uri;
                }
            } else {
                uri = address;
            }
        }
        if (uri != null) {
            buffer.getAddress().append(uri);
            String query = (String) message.get(Message.QUERY_STRING);
            if (query != null) {
                buffer.getAddress().append("?").append(query);
            }
        }

        if (!isShowBinaryContent() && isBinaryContent(contentType)) {
            buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');

            log(logger, buffer.toString());
            auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog("UDLoggingInInterceptor", timestampGenerator.getCurrentTimeStamp(), message));
        } else if(null != contentType && !"application/json".equalsIgnoreCase(contentType)){
            buffer.getMessage().append("Payload not logged: not of type 'application/json'").append('\n');

            log(logger, buffer.toString());
            auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog("UDLoggingInInterceptor", timestampGenerator.getCurrentTimeStamp(), message));
        } else {
            InputStream is = message.getContent(InputStream.class);
            if (is != null && displayPayload) {
                logInputStream(message, is, buffer, encoding, contentType);
                auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog("UDLoggingInInterceptor", timestampGenerator.getCurrentTimeStamp(), message));
            } else {
                Reader reader = message.getContent(Reader.class);
                if (reader != null) {
                    auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog("UDLoggingInInterceptor", timestampGenerator.getCurrentTimeStamp(), message));
                    logReader(message, reader, buffer);
                }
            }
            log(logger, formatLoggingMessage(buffer));
            auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog("UDLoggingInInterceptor", timestampGenerator.getCurrentTimeStamp(), message, buffer,0));
        }
    }

	static String getSecurityAuditRecordText(Map<String, String> securityData) {
		StringBuilder securityAuditLogRecord = new StringBuilder();
		List<String> securityAuditFields = Arrays.asList(headerBlacklist.split(","));
		for (Map.Entry<String, String> securityField : securityData.entrySet()) {
			if (!securityAuditFields.contains(securityField.getKey())) {
				securityAuditLogRecord.append(securityField.getKey()).append("=").append(securityField.getValue()).append("|");
			}
		}
		return securityAuditLogRecord.toString();
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

    public String getHeaderBlacklist() {return headerBlacklist;}
    public void setHeaderBlacklist(String headerBlacklist) {
        this.headerBlacklist = headerBlacklist;
    }

    public boolean isDisplayPayload() {return displayPayload;}
    public void setDisplayPayload(boolean displayPayload) {this.displayPayload = displayPayload; }
}