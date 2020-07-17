package uk.gov.gsi.justice.spg.dr.client.validators;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultiDeleteValidator {

    private static final Log log = LogFactory.getLog(MultiDeleteValidator.class);

    public static boolean isValidMultitedeleteCommand(String[] args) {
        if (args.length < 1) {
            log.info("You must enter at least 2 valid arguments - multidelete and docIds");
            log.info("Example ==>  multidelete docIds=1,2,3");
            return false;
        }
        String [] docids = args[1].split("=");
        if (!docids[0].equals("docIds")){
            log.info("Please enter valid argument: " + docids[0]);
            return false;
        }
        return true;
    }
}
