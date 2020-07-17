package uk.gov.gsi.justice.spg;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreInvokeInterceptor extends AbstractPhaseInterceptor<Message> {

	private Logger log = LoggerFactory.getLogger(PreInvokeInterceptor.class);
	
	public PreInvokeInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {

		/*
		 * Find the Content-Type header map entry
		 * Remove the "boundary=..." substring if it is a multipart/* mime type
		 * This is because the cxf SendInterceptor appends its own boundary= token to the Content-Type without removing any existing one
		 */
		Map<String,List> headerMap = (Map<String, List>)message.get(Message.PROTOCOL_HEADERS);
		List<String> contentTypeList = headerMap.get(Message.CONTENT_TYPE);

		Map<String,List> headerMap1 = (Map<String, List>)message.get(Message.PROTOCOL_HEADERS);
		headerMap1.remove("Authorization");
		message.put(Message.PROTOCOL_HEADERS, headerMap1);

		List<String> replaceContentType = new ArrayList<>();
		StringBuilder newMimeType = new StringBuilder();

		for(String ct : contentTypeList) {

			if(ct != null && ct.contains("multipart")) {
				
				log.debug("Remove any existing boundary string in multipart content-type");
				
				String[] tokens = ct.split(";");
				for(String s : tokens) {
					if(!s.contains("boundary=")){
						newMimeType.append(s + ";");
					}
				}
				log.debug("new mime type is <" + newMimeType + ">");
				
				replaceContentType.add(newMimeType.toString());
				
			} else {
				replaceContentType.add(ct);
			}
		}
		headerMap.put(Message.CONTENT_TYPE, replaceContentType);
		
	}

}
