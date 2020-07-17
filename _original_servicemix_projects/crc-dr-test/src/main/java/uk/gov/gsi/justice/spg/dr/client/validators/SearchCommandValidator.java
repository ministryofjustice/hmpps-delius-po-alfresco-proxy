package uk.gov.gsi.justice.spg.dr.client.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class SearchCommandValidator {

    private static final Log log = LogFactory.getLog(SearchCommandValidator.class);

    //Do not change the order of this list
    private final static List<String> validParams = Arrays.asList("from", "to", "entityType", "entityId", "docType", "pageSize", "maxResults", "startIndex", "sort");
    public final static List<String> validEntityType = Arrays.asList("APREFERRAL", "REFERRAL", "COURTREPORT", "INSTITUTIONALREPORT", "CONTACT", "OFFENDER", "PERSONALCONTACT", "EVENT", "ADDRESSASSESSMENT", "ASSESSMENT", "PROCESSCONTACT", "RATECARDINTERVENTION");



    public static boolean isValidSearchCommand(String[] args) {
        //We must pass at least 2 parameters  - search and crn. Query params are optional
        if (args.length < 2) {
            log.info("You must enter at least 2 valid arguments - search and unique offender ID");
            return false;
        }
            //Make sure that the second argument is CRN
            String[] queryParams1 = args[1].split("=");
            if (queryParams1.length == 2 || !ValidatorUtils.isValidCRN(args[1])) {
                log.info("Please enter valid CRN parameter");
                return false;
            }

        //check for valid query params
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {

                String[] queryParams = args[i].split("=");

                if (!validParams.contains(queryParams[0]) || queryParams.length != 2) {
                    log.info("You must enter valid query parameter: " + queryParams[0]);
                    return false;
                }

                if (queryParams[0].equals(validParams.get(0)) || queryParams[0].equals(validParams.get(1))) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    format.setLenient(false);
                    try {
                        format.parse(queryParams[1]);
                    } catch (ParseException e) {
                        log.info("400: Invalid date format: " + queryParams[1] + " for query parameter: " + queryParams[0]);
                        return false;
                    }
                }

                if (queryParams[0].equals(validParams.get(2)) && !validEntityType.contains(queryParams[1])) {
                    log.info("Please enter valid entity type");
                    log.info("Valid entity types are: " + validEntityType.toString());
                    return false;
                }

                if (queryParams[0].equals(validParams.get(4)) && !ValidatorUtils.validDocTypeId.contains(queryParams[1])) {
                    log.info("Please enter valid document type");
                    log.info("Valid entity types are: " + ValidatorUtils.validDocTypeId.toString());
                    return false;
                }

                if (queryParams[0].equals(validParams.get(3)) ||
                        queryParams[0].equals(validParams.get(5)) ||
                        queryParams[0].equals(validParams.get(6)) ||
                        queryParams[0].equals(validParams.get(7))) {

                    if (!ValidatorUtils.isLong(queryParams[1])) {
                        log.info("Please enter valid number for: " + queryParams[0]);
                        return false;
                    }
                    if (Long.parseLong(queryParams[1]) < 0) {
                        log.info("Please enter valid positive number for: " + queryParams[0]);
                        return false;
                    }
                }
                if (queryParams[0].equals(validParams.get(8)) && !ValidatorUtils.validSortParams.contains(queryParams[1])) {
                    log.info("Please enter valid sort parameter");
                    log.info("Valid sort parameters are: " + ValidatorUtils.validSortParams.toString());
                    return false;
                }
            }
        }
        return true;
    }
}
