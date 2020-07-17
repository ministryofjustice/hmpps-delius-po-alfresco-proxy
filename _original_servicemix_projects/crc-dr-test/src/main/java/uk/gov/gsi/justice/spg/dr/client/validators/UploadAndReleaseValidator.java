package uk.gov.gsi.justice.spg.dr.client.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

public class UploadAndReleaseValidator {

    private static final Log log = LogFactory.getLog(UploadNewValidator.class);

    public final static List<String> validEntityType = Arrays.asList("APREFFERAL", "REFERRAL", "COURTREPORT", "INSTITUTIONALREPORT", "CONTACT", "OFFENDER", "PERSONALCONTACT", "EVENT", "ADDRESSASSESSMENT", "ASSESSMENT", "PROCESSCONTACT", "RATECARDINTERVENTION");

    private final static List<String> validParams = Arrays.asList("author", "entityType", "entityId", "docType");

    public static boolean isValidUploadAndRelease(String[] args) {
        if (args.length < 4) {
            log.info("You must enter at least 3 valid arguments - DOC_ID ,fileData, and author");
            log.info("Example ==>  uploadandrelease 123Doc test.doc author=test");
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

            if (queryParams[0].equals(validParams.get(2)) && !ValidatorUtils.isLong(queryParams[1])){
                log.info("Please enter valid entity id");
                return false;
            }

            if (queryParams[0].equals(validParams.get(3)) && !ValidatorUtils.validDocTypeId.contains(queryParams[1])) {
                log.info("Please enter valid document type");
                log.info("Valid entity types are: " + ValidatorUtils.validDocTypeId.toString());
                return false;
            }
        }
        return true;
    }


}
