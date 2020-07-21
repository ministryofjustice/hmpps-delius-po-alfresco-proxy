package uk.gov.gsi.justice.po.alfresco.proxy.spg; /**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.oauth.*;
import net.oauth.OAuth.Parameter;
import net.oauth.signature.RSA_SHA1;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.oauth.filters.AbstractAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.audit.UDAuditLogBuilder;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.audit.UDInterchangeAuditLogService;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.audit.UDSPGLogTypes;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.exceptions.InterchangeSenderPermissionDeniedException;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.exceptions.UDSPGExceptions;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.utils.AuthUtils;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.utils.PropertyResolver;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.utils.TimestampGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import java.net.ConnectException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

import static uk.gov.gsi.justice.po.alfresco.proxy.spg.UDLoggingInInterceptor.getSecurityAuditRecordText;
import static uk.gov.gsi.justice.po.alfresco.proxy.spg.utils.KeystoreFactory.getFieldValueFromCertificateSubjectDN;
//import static uk.gov.gsi.justice.spg.UDLoggingInInterceptor.getSecurityAuditRecordText;
//import static uk.gov.gsi.justice.spg.utils.KeystoreFactory.getFieldValueFromCertificateSubjectDN;

/**
 * JAX-RS OAuth filter which can be used to protect end user endpoints
 */
@Provider
@PreMatching
public class OAuthRequestFilter extends AbstractAuthFilter implements ContainerRequestFilter {

	private Logger log = LoggerFactory.getLogger(OAuthRequestFilter.class);
	private PropertyResolver propertyResolver;
	private UDInterchangeAuditLogService auditLogService;
	private TimestampGenerator timestampGenerator;

	private static final String CERTIFICATE_CN_TO_SENDER_ID_CFG_PREFIX = "certificate.";
	private static final String CALLING_VIA_PROXY_URL_REWRITE = "CALLING_VIA_PROXY_URL_REWRITE.";
	private static final String DISABLED_SENDER_IDS_CFG_PROPERTY_NAME = "spg.disabled.senderids";
	private static final String LOGGING_ID = "ID";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String HEADER_REMOTE_USER = "X-DocRepository-Remote-User";
	private static final String HEADER_REAL_USER = "X-DocRepository-Real-Remote-User";
	private static final String HEADER_NOT_FOUND = "Not Found";

	private final String oauthProtocol;
	private String trustStoreFile;
	private String trustStorePassword;
	private String headerBlacklist;
	private boolean logSecurity;

	public OAuthRequestFilter(String oauthProtocol) {
		this.oauthProtocol = oauthProtocol;
	}

	public void filter(ContainerRequestContext context) {
		Map<String, String> securityData = new LinkedHashMap<>();
		String senderId = null;
		Message message = null;

		try {
			message = JAXRSUtils.getCurrentMessage();

			String id = (String)message.get(LoggingMessage.ID_KEY);
			securityData.put(LOGGING_ID, id);

			MessageContext mc = new MessageContextImpl(message);
			HttpServletRequest req = mc.getHttpServletRequest();

			List<String> headerList = new ArrayList<>();
			if (req != null) {

				Enumeration e = req.getHeaders(HEADER_REMOTE_USER);

				while (e.hasMoreElements()) {
					headerList.add((String) e.nextElement());
				}

				if (headerList.isEmpty()) {
					log.info(HEADER_REMOTE_USER + " not found");
					securityData.put(HEADER_REMOTE_USER, HEADER_NOT_FOUND);
				} else {
					senderId = headerList.get(0);
					securityData.put(HEADER_REMOTE_USER, senderId);
				}

				headerList.clear();
				e = req.getHeaders(HEADER_REAL_USER);

				while (e.hasMoreElements()) {
					headerList.add((String) e.nextElement());
				}

				if (headerList.isEmpty()) {
					log.info(HEADER_REAL_USER + " not found");
					securityData.put(HEADER_REAL_USER, HEADER_NOT_FOUND);
				} else {
					securityData.put(HEADER_REAL_USER, headerList.get(0));
				}

				headerList.clear();
				e = req.getHeaders(HEADER_AUTHORIZATION);
				while (e.hasMoreElements()) {
					headerList.add((String) e.nextElement());
				}
			}
			else {
				log.error("request is null ");
				throw new IllegalArgumentException("Request should not be null");
			}

			String authorization;
			if (headerList.size() == 0) {
				log.info(HEADER_AUTHORIZATION + " " + HEADER_NOT_FOUND);
				securityData.put(HEADER_AUTHORIZATION, HEADER_NOT_FOUND);
				context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").build());
				auditLogService.createUDAlertRecord(getSecurityAuditRecordText(securityData));
				return;
			} else {
				authorization = headerList.get(0);
			}

			// https://opensocial.atlassian.net/wiki/display/OSREF/Validating+Signed+Requests
			List<OAuth.Parameter> listOfOauthParams = OAuthMessage.decodeAuthorization(authorization);


			for (Parameter parameter : listOfOauthParams) {
				log.info("DEBUG*** oauthparam from decodeAuthorisation: "+parameter.getKey()+" = "+parameter.getValue());
			}

			for (Object e : req.getParameterMap().entrySet()) {
				Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;

				for (String value : entry.getValue()) {
					listOfOauthParams.add(new OAuth.Parameter(entry.getKey(), value));
					log.info("DEBUG*** oauthparam reqParam Map:?DEBUG "+entry.getKey()+" = "+value);
				}
			}

			for (Parameter parameter : listOfOauthParams) {
				securityData.put(parameter.getKey(), parameter.getValue());
//				log.info("oauthparam "+parameter.getKey()+" = "+parameter.getValue());
			}



			// For some reason the protocol in the request url might be changed to http even if the original request was made to a loadbalancer via
			// to a loadbalancer via httpsuk.gov.gsi.justice.spg.utils.. This means the signature verification won't work as the client would have signed with the https
			// version of the url but we are verifying with a http url. So we allow the user to force the url we verify with
			// to be either the https (default) or http version.
			String requestURL = req.getRequestURL().toString();
			if (this.oauthProtocol == null || this.oauthProtocol.trim().isEmpty() || this.oauthProtocol.equalsIgnoreCase("https")) {
				// assume protocol is https
				requestURL = requestURL.replaceFirst("http://", "https://");
			}
			else {
				requestURL = requestURL.replaceFirst("https://", "http://");
			}


			//we also need to mix and match psn address vs non psn addresses, the spg is expecting signatures signed against
			// spgw-ext(.subdomain).probation.service.justice.gov.uk
			// but on psn its
			// spgw-ext-psn(.subdomain).probation.service.justice.gov.uk

			OAuthMessage oauthMessage;
			OAuthValidator validator;

			log.info("Intialising oauth");
			oauthMessage = new OAuthMessage(req.getMethod(), requestURL, listOfOauthParams);

			log.info("Initialising oauth validator");
			validator = new SimpleOAuthValidator();
			OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
			OAuthConsumer consumer = new OAuthConsumer(null, oauthMessage.getConsumerKey(), null, serviceProvider);

			log.info("Getting certificate");
			Certificate certificate = AuthUtils.getCertificate(trustStoreFile, trustStorePassword, oauthMessage.getConsumerKey());
			PublicKey publicKey = certificate.getPublicKey();
			log.info("Getting Common Name");
			String commonName = getFieldValueFromCertificateSubjectDN("CN", (X509Certificate)certificate);

			log.info("Determining from PSN by common name "+commonName);
			String proxyUrlRewritePattern = getProxyUrlRewritePattern(commonName);

			log.info("requestURL = "+requestURL);
			if (proxyUrlRewritePattern!=null && proxyUrlRewritePattern.length()>0  ) {
				//note this is hard to test in the current spg-all-200 docker deploy, as it doesn't like subdomains for some reason
				String[] pattern = proxyUrlRewritePattern.split(",");
				log.info("search string = "+pattern[0]);
				log.info("replace string = "+pattern[1]);

				requestURL = requestURL.replace(pattern[0], pattern[1]);

				log.info("requestURL now = "+requestURL);

				//repeat above process
				log.info("Intialising oauth again with new url, as had to load it earlier to determine cert details");
				oauthMessage = new OAuthMessage(req.getMethod(), requestURL, listOfOauthParams);

				log.info("Initialising oauth validator");
				validator = new SimpleOAuthValidator();
				serviceProvider = new OAuthServiceProvider(null, null, null);
				consumer = new OAuthConsumer(null, oauthMessage.getConsumerKey(), null, serviceProvider);

			}


			securityData.put("certificateType", certificate.getType());
			securityData.put("keyAlgorithm", publicKey.getAlgorithm());
			securityData.put("keyFormat", publicKey.getFormat());
			securityData.put("keyIssuer", ((X509Certificate) certificate).getIssuerDN().getName());

			consumer.setProperty(RSA_SHA1.PUBLIC_KEY, publicKey);

			OAuthAccessor accessor = new OAuthAccessor(consumer);





			log.info ("Security details: "+getSecurityAuditRecordText(securityData));



			log.info("Validating Message");
			validator.validateMessage(oauthMessage, accessor);

			String[] permittedSenderIds = getPermittedSenderIds(commonName);

			if (!Arrays.asList(permittedSenderIds).contains(senderId)) {
				// sender id is not permitted for this certificate
				throw new InterchangeSenderPermissionDeniedException("Certificate with common name: " + commonName + " is not permitted to send as sender id: " + senderId +". Permitted sender ids are: " + Arrays.toString(permittedSenderIds));
			}

			Set<String> disabledSenderIds = getDisabledSenderIds();
			if (disabledSenderIds.contains(senderId)) {
				log.info("Sender is disabled: " + senderId);
				throw new UDSPGExceptions.SenderDisabledException("Sender has been disabled");
			}
		} catch (OAuthProblemException e) {
			log.info("OAuthProblemException", e);
			auditLogService.createUDAlertRecord(getSecurityAuditRecordText(securityData));
			context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").build());
		}
		catch (UDSPGExceptions.SenderDisabledException e) {
			log.info("absorbing messagecompile group: 'org.apache.cxf', name: 'cxf-rt-frontend-jaxrs', version: '3.0.2'");
			auditLogService.createUDAuditRecord(UDAuditLogBuilder.createAuditLog(UDSPGLogTypes.LOG_PROXY_UD_ABSORBED_MESSAGE.toString(), timestampGenerator.getCurrentTimeStamp(), message)); //??? what should this be?
			context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").build());
		} catch (ConnectException e) {
			log.info("Connection Refused Exception", e);
			auditLogService.createUDAlertRecord("Connection Exception occurred ");
			auditLogService.createUDAlertRecord(e.getMessage());
			auditLogService.createUDAlertRecord(getSecurityAuditRecordText(securityData));
			context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").build());
		} catch (InterchangeSenderPermissionDeniedException ie) {
			auditLogService.createUDAlertRecord(ie.toString());
			context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").entity(ie.getMessage()).type(MediaType.APPLICATION_JSON).build());
		} catch (Exception e) {
			log.info("Exception", e);
			auditLogService.createUDAlertRecord("Exception occurred for Status Code 401 :");
			auditLogService.createUDAlertRecord(e.getMessage());
			auditLogService.createUDAlertRecord(getSecurityAuditRecordText(securityData));
			context.abortWith(Response.status(401).header("WWW-Authenticate", "OAuth").build());
		}
		finally {
			if(logSecurity){
				auditLogService.createUDSecurityAuditRecord(getSecurityAuditRecordText(securityData));
			}
		}
	}

	private String getProxyUrlRewritePattern(String commonName) {
		String proxyConnectionType =  propertyResolver.getProperty(CALLING_VIA_PROXY_URL_REWRITE + commonName);
		log.info(commonName+" if present replaces first with second eg (spgw-ext.probation|spgw-int-psn.pre-prod.probation): "+proxyConnectionType);
		return proxyConnectionType;
	}

	private String[] getPermittedSenderIds(String commonName) {
		String[] ret = new String[0];
		String senderIdsString = propertyResolver.getProperty(CERTIFICATE_CN_TO_SENDER_ID_CFG_PREFIX + commonName);
		if (senderIdsString !=null){
			ret = senderIdsString.split(",");
		}
		return ret;
	}

	private Set<String> getDisabledSenderIds() {
		Set<String> disabledSenderIdSet = new HashSet<>();
		String disabledSenderIdsProperty = propertyResolver.getProperty(DISABLED_SENDER_IDS_CFG_PROPERTY_NAME, "");
		String[] disabledSenderIds = disabledSenderIdsProperty.split(",");
		disabledSenderIdSet.addAll(Arrays.asList(disabledSenderIds));
		return disabledSenderIdSet;
	}

	/*BEANS*/
	public UDInterchangeAuditLogService getAuditLogService() {
		return auditLogService;
	}
	public void setAuditLogService(UDInterchangeAuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	public String getTrustStoreFile() {
		return trustStoreFile;
	}
	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	public void setTimestampGenerator(TimestampGenerator timestampGenerator) { this.timestampGenerator = timestampGenerator; }
	public TimestampGenerator getTimestampGenerator() {
		return timestampGenerator;
	}

	public String getHeaderBlacklist() {
		return headerBlacklist;
	}
	public void setHeaderBlacklist(String headerBlacklist) {
		this.headerBlacklist = headerBlacklist;
	}

	public void setLogSecurity(boolean logSecurity) {
		this.logSecurity = logSecurity;
	}
}
