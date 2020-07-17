# TODO - delete this section if not needed. 30/03/2020 PC
# old notes relating to "Install the following automatically through karaf features"
#install -s mvn:org.codehaus.jackson/jackson-core-asl/1.9.13
#install -s mvn:org.codehaus.jackson/jackson-mapper-asl/1.9.13
#feature:install cxf-rs-security-oauth



proxy overview
**************

Single camel route that creates the context, beans + server and client tied to the SPGService



SPGService models the a subset of available "noms-spg" API calls that a PO can make to alfresco
 note this does not include the "spg-admin" API calls, nor some utility calls such as `updatemodifieddate`



Alfresco api:
https://alfresco.dev.delius-core.probation.hmpps.dsd.io/alfresco/service/index/family/NOMS-SPG



Interceptors:

Before and after the calls are completed, various logging is performed by interceptors.

Antivirus & Oauth verification are also performed as part of inbound interceptors.



SSL:
SSL trusted certs are obtained from the standard oneTrustStore.jks packaged with the servicemix build.


Config:
Uses spg.proxy.cfg for shared config about PO certs
Uses spg.proxy.ud.cfg for specific UD proxy settings