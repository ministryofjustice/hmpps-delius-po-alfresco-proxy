Packaging and Deployment
========================
The test client for the SPG Unstructured Data service is packaged as a java jar file that contains all dependent libraries.
This jar file is deployed to the root of the servicemix deployment (e.g. /opt/spg/servicemix or c:\crc-dist\spg\servicemix)


ADDITIONAL INSTALLATION STEPS
==============================
You will need to add the oath features to servicemix by logging into the client and entering:

feature:install cxf-rs-security-oauth



Configuration
=============
The configuration of the test client is contained in the file spg.dr.client.cfg in the etc folder of the servicemix installation.
It contains the following entries: -

	spg.dr.scheme = https
	spg.dr.host = localhost

	#either stub endpoint
	#spg.dr.port = 8181
	#spg.dr.endpoint = /cxf/alfresco

	#or proxy endpoint
	spg.dr.port = 9001
	spg.dr.endpoint = /cxf/spg-proxy-ud

	#change serviceaccount to your CRC, and the specific end user to username
	spg.dr.serviceaccount.username=C00
	spg.dr.real.username=JaneBloggs

	spg.dr.keystore = etc/keystores/crcKeystore.jks
	spg.dr.keystore.password = *masked*
	spg.dr.keystore.privateKey.alias=crc
	spg.dr.truststore = etc/keystores/truststore.jks
	spg.dr.truststore.password = *masked*
	
The client will send all requests to the endpoint specified in the spg.dr.endpoint.
The spg-proxy-ud endpoint service can be configured to forward the client request to the SPG Alfresco Stub service or the real Alfresco Document Repository service.


******* HTTPS and Request Signing *************

If the spg-proxy-ud service is configured to accept requests over https and using mutual authentication, then you can change the spg.dr.scheme to be "https" and the spg.dr.port to be "9001"

The mutual authentication and signing will depend on the correct configuration of the client and server certificates in the keystore and truststore files.


Using the test client 
=====================

The client can be run by going to a windows or linux command prompt in the servicemix folder and issuing the following commands.
Each command should return a 200 HTTP status code if the request is successful.


1. A basic connectivity test

java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - ping"}>

2. The search API

Usage: search <CRN>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar search C1234566
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/search/C1234566
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - search "}>

3. The uploadnew API

Usage: uploadnew <CRN> <file> <author> <entityType> <entityId> <docType>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar uploadnew C123456 "c:\Temp\Lorem_ipsum.txt" author entityType entityId docType
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/uploadnew
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - uploadnew for: CRN <C123456> author <author> entityType <entityType> entityId <entityId> docType <docType"}>

Available entity types as of xsd 0-9-4

			<xs:enumeration value="APREFFERAL"/>
			<xs:enumeration value="REFERRAL"/>
			<xs:enumeration value="CASE_ALLOCATION"/>
			<xs:enumeration value="COURTREPORT"/>
			<xs:enumeration value="INSTITUTIONALREPORT"/>
			<xs:enumeration value="CONTACT"/>
			<xs:enumeration value="OFFENDER"/>
			<xs:enumeration value="PERSONALCONTACT"/>
			<xs:enumeration value="EVENT"/>
			<xs:enumeration value="ADDRESSASSESSMENT"/>
			<xs:enumeration value="ASSESSMENT"/>
			<xs:enumeration value="PROCESSCONTACT"/>
			<xs:enumeration value="RATECARDINTERVENTION"/>
			<xs:enumeration value="PERSONALCIRCUMSTANCE"/>

Doc types as of xsd 0-9-4

    DOCUMENT,
    PREVIOUS_CONVICTION,
    CPS_PACK;



The file will be uploaded to <servicemix>\data\spg-dr-stub folder

4. The fetch API
	
Usage: fetch <DOC_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar fetch 1462283543083_C123456 mylocalfilename
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/fetch/C123456
	HTTP status code : 200
	Output from Server .... <Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.>
	
A file that is embedded within the jar is streamed to the output

5. The fetchandreserve API

Usage: fetchandreserve <DOC_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar fetchandreserve 1462283543083_C123456 mylocalfilename
	Using properties configuration file <etc/spg.dr.client.cfg
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/fetchandreserve/C123456
	HTTP status code : 200
	Output from Server .... <Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.>

6. The uploadandrelease API

Usage: uploadandrelease <DOC_ID> <file> <author>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar uploadandrelease 1462283543083_C123456 c:\Temp\Lorem_ipsum.txt author
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/uploadandrelease/C123456
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - uploadandrelease "}>
	
7. The release API

Usage: release <DOC_ID>
	java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar release 1462283543083_C123456
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/release/C123456
	Dec 30, 2015 2:42:37 PM org.glassfish.jersey.client.JerseyInvocation validateHttpMethodAndEntity
	WARNING: Entity must not be null for http method PUT.
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - release "}>

8. The delete API

Usage: delete <DOC_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar delete 1462283543083_C123456
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/delete/C123456
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - delete "}>

9. The deleteall API

Usage: deleteall <CRN_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar deleteall C123456
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/deleteall/C123456
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - deleteall "}>
	
10. The lock API

Usage: lock <DOC_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar lock 1462283543083_C123456
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/lock/C123456
	Dec 30, 2015 2:45:40 PM org.glassfish.jersey.client.JerseyInvocation validateHttpMethodAndEntity
	WARNING: Entity must not be null for http method PUT.
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - lock "}>

12. The declareasrecord API

Usage: declareasrecord <DOC_ID>
java -jar crc-dr-test-1.1.0.271-jar-with-dependencies.jar declareasrecord 1462283543083_C123456
	Using properties configuration file <etc/spg.dr.client.cfg>
	http://localhost:8282/cxf/spg-proxy-ud
	http://localhost:8282/cxf/spg-proxy-ud/declareasrecord/C123456
	HTTP status code : 200
	Output from Server .... <{"message" : "Alfresco Stub - declareasrecord "}>