package uk.gov.gsi.justice.spg.audit;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDAuditLogBuilder {

    private static Logger log = LoggerFactory.getLogger(UDAuditLogBuilder.class);


    public static String createAuditLog(String logType, String timestamp, Message message,long roundTripTimeMillis) {

        return createAuditLog(logType, timestamp, message, null,roundTripTimeMillis);
    }

    public static String createAuditLog(String logType, String timestamp, Message message) {

        return createAuditLog(logType, timestamp, message, null,0);
    }

    public static String createAuditLog(String logType, String timestamp, Message message, LoggingMessage buffer, long roundTripTimeMillis) {

        Map<String,List> headerMap = (Map<String, List>)message.get(Message.PROTOCOL_HEADERS);
        String from = headerMap==null ? "ALF" :(headerMap.containsKey("X-DocRepository-Remote-User") ? String.valueOf(headerMap.get("X-DocRepository-Remote-User")) : "ALF");

        Map<String, Object> map = new HashMap<>();

        if(buffer != null){
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = buffer.getPayload().toString();
                map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String to = message.containsKey(Message.INBOUND_MESSAGE) ? "" : "ALF";
        String uri = getMessageValue(message, Message.REQUEST_URI);
        String base = getMessageValue(message, Message.BASE_PATH);
        String messageType = uri.replace(base,"");

        StringBuilder auditLogEntry = new StringBuilder();
        auditLogEntry.append(timestamp).append("|").append(logType).append("|");
        auditLogEntry.append("|").append(UDSPGLogFields.UD_FROM).append("=").append(from);
        auditLogEntry.append("|").append(UDSPGLogFields.UD_TO).append("=").append(to);
        auditLogEntry.append("|").append(UDSPGLogFields.UD_ID).append("=").append(message.get(LoggingMessage.ID_KEY));
        auditLogEntry.append("|").append(UDSPGLogFields.UD_MESSAGE_TYPE).append("=").append(messageType);
        if (roundTripTimeMillis > 0) {
            auditLogEntry.append("|").append(UDSPGLogFields.UD_ROUNDTRIP_TIME).append("=").append(Long.valueOf(roundTripTimeMillis));
        }

        auditLogEntry.append("|").append("INBOUND_MESSAGE").append("=").append(getMessageValue(message, Message.INBOUND_MESSAGE));
        auditLogEntry.append("|").append("HTTP_REQUEST_METHOD").append("=").append(getMessageValue(message, Message.HTTP_REQUEST_METHOD ));
        auditLogEntry.append("|").append("REQUEST_URL").append("=").append(getMessageValue(message, Message.REQUEST_URL ));
        auditLogEntry.append("|").append("RESPONSE_CODE").append("=").append(getMessageValue(message, Message.RESPONSE_CODE ));
        if (buffer != null) {
            if(message.get(UDSPGLogFields.UD_CRN.toString()) == null)
                message.put(UDSPGLogFields.UD_CRN.toString(), "");
            auditLogEntry.append("|").append("PAYLOAD").append("=").append(buffer.getPayload());
            auditLogEntry.append("|").append(UDSPGLogFields.UD_CRN).append("=").append(map.get("CRN"));
        }

        if(message.get(UDSPGLogFields.UD_ERRDESC.toString())!= null) {
            if(message.get(UDSPGLogFields.UD_CRN.toString()) == null)
               message.put(UDSPGLogFields.UD_CRN.toString(), "");
            auditLogEntry.append("|").append(UDSPGLogFields.UD_ERRDESC.toString()).append("=").append(message.get(UDSPGLogFields.UD_ERRDESC.toString()));
            auditLogEntry.append("|").append(UDSPGLogFields.UD_CRN.toString()).append("=").append(message.get(UDSPGLogFields.UD_CRN.toString()));
        }

        return auditLogEntry.toString();
    }

    private static String getMessageValue(Message message, String messageKey) {
        return message.get(messageKey) !=null ? message.get(messageKey).toString() : "" ;
    }
}
