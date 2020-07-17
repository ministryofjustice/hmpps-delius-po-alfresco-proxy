package uk.gov.gsi.justice.spg.dr.client.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;


public class UpdateMetadataValidator {

    private static final Log log = LogFactory.getLog(SearchCommandValidator.class);

    //Do not change the order of this list
    private final static List<String> validParams = Arrays.asList("fileName", "entityType","author", "entityId", "docType");
    public final static List<String> validEntityType = Arrays.asList("APREFFERAL","APREFERRAL", "REFERRAL", "COURTREPORT", "INSTITUTIONALREPORT", "CONTACT", "OFFENDER", "PERSONALCONTACT", "EVENT", "ADDRESSASSESSMENT", "ASSESSMENT", "PROCESSCONTACT", "RATECARDINTERVENTION");


    public static boolean isValidUpdateMetadataCommand(String[] args) {

        //We must pass at least 3 parameters  - updatemetadata and document id and at least 1 query parameter
        if (args.length < 3) {
            log.info("You must enter at least 3 arguments - updatemetadata , document id and at least one optional parameter - fileName, entityType, entityId, docType, author");
            return false;
        }

        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                String[] queryParams = args[i].split("=");

                if (!validParams.contains(queryParams[0].trim())) {
                    log.info("You must enter valid query parameter: " + queryParams[0]);
                    return false;
                }

                if (queryParams[0].trim().equals(validParams.get(1)) && !validEntityType.contains(queryParams[1].trim())) {
                    log.info("Please enter valid entity type");
                    log.info("Valid entity types are: " + validEntityType.toString());
                    return false;
                }

                if (queryParams[0].trim().equals(validParams.get(3))) {
                    if (!ValidatorUtils.isLong(queryParams[1])) {
                        log.info("Please enter valid number for: " + queryParams[0]);
                        return false;
                    }
                    if (Long.parseLong(queryParams[1]) < 0) {
                        log.info("Please enter valid positive number for: " + queryParams[0]);
                        return false;
                    }
                }

                if (queryParams[0].trim().equals(validParams.get(4)) && !ValidatorUtils.validDocTypeId.contains(queryParams[1].trim())) {
                    log.info("Please enter valid document type");
                    log.info("Valid entity types are: " + ValidatorUtils.validDocTypeId.toString());
                    return false;
                }
            }
        }
        return true;
    }

}
