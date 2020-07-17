package uk.gov.gsi.justice.spg.dr.client.validators;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

public class UploadNewValidator {
    private static final Log log = LogFactory.getLog(UploadNewValidator.class);

    private final static List<String> validParams = Arrays.asList("author", "entityType", "entityId", "docType", "locked", "reserved");
    public final static List<String> validEntityType = Arrays.asList(   "ADDRESSASSESSMENT",
                                                                        "APREFFERAL",
                                                                        "ASSESSMENT",
                                                                        "CASE_ALLOCATION",
                                                                        "CONTACT",
                                                                        "COURTREPORT",
                                                                        "EVENT",
                                                                        "INSTITUTIONALREPORT",
                                                                        "OFFENDER",
                                                                        "PERSONALCIRCUMSTANCE",
                                                                        "PERSONALCONTACT",
                                                                        "PROCESSCONTACT",
                                                                        "RATECARDINTERVENTION",
                                                                        "REFERRAL",
                                                                        "UPWAPPOINTMENT");

    public static boolean isValidUploadNewCommand(String[] args) {
        if (args.length < 7) {
            log.info("You must enter at least 6 valid arguments - CRN, filename ,author, entityType, entityId and docType");
            log.info("Example ==>  uploadnew X030927 test.doc author=test entityType=REFERRAL entityId=1 docType=DOCUMENT");
            return false;
        }

        if (!ValidatorUtils.isValidCRN(args[1])) {
            log.info("Please enter valid CRN parameter");
            return false;
        }

        for (int i = 3; i < args.length; i++) {
            String[] queryParams = args[i].split("=");

            if (!validParams.contains(queryParams[0]) || queryParams.length != 2) {
                log.info("You must enter valid query parameter: " + queryParams[0]);
                return false;
            }

            if (queryParams[0].equals(validParams.get(1)) && !validEntityType.contains(queryParams[1])) {
                log.info("Please enter valid entity type");
                log.info("Valid entity types are: " + validEntityType.toString());
                return false;
            }

            if (queryParams[0].equals(validParams.get(2)) && !ValidatorUtils.isLong(queryParams[1])) {
                log.info("Please enter valid number for: " + queryParams[0]);
                return false;
            }


            if (queryParams[0].equals(validParams.get(3)) && !ValidatorUtils.validDocTypeId.contains(queryParams[1])) {
                log.info("Please enter valid document type");
                log.info("Valid entity types are: " + ValidatorUtils.validDocTypeId.toString());
                return false;
            }

            if ((queryParams[0].equals(validParams.get(4)) || queryParams[0].equals(validParams.get(5))) && !ValidatorUtils.isBoolean(queryParams[1]) ){
                log.info("Please enter valid boolean value for " + queryParams[0]);
                return false;
            }
        }
        return true;
    }


}
