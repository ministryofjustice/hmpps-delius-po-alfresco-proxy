package uk.gov.gsi.justice.po.alfresco.proxy.spg.spg_core_rest;

import java.util.logging.Logger;

public class TestPattern {

       static Logger log = Logger.getLogger("this");

    public static void main(String... args) {

        System.out.println("Hi");
        String psnConnectionType = "spgw-ext.probation,spgw-int-psn.pre-prod.probation";
        String requestURL = "https://spgw-ext.probation.service.justice.gov.uk:9001/cxf/spg-proxy-ud/search/X000000";
        log.info("requestURL = " + requestURL);
        //note this is hard to test in the current spg-all-200 docker deploy, as it doesn't like subdomains for some reason
        String[] pattern = psnConnectionType.split(",");
        log.info("search string = " + pattern[0]);
        log.info("replace string = " + pattern[1]);

        requestURL = requestURL.replace(pattern[0], pattern[1]);
        log.info("new requestURL = " + requestURL);

    }
}
